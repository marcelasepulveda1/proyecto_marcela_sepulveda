package com.example.panalsuite.data.repository

import com.example.panalsuite.data.local.SeedTickets
import com.example.panalsuite.data.local.TicketLocalDataSource
import com.example.panalsuite.data.remote.PanalSuiteApi
import com.example.panalsuite.data.remote.dto.HistoryDto
import com.example.panalsuite.data.remote.dto.TicketDto
import com.example.panalsuite.data.remote.dto.toDomain
import com.example.panalsuite.model.Category
import com.example.panalsuite.model.Priority
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketAction
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/** Result of talking to the API: the change reached the server, or it was only saved on the phone. */
enum class SyncResult { SYNCED, OFFLINE }

/** Data typed by the user in the "new ticket" form. */
data class TicketDraft(
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val branch: String
)

data class CreateResult(val ticketId: String, val syncResult: SyncResult)

/**
 * Single source of truth for tickets (offline first).
 * The screens always read the local copy; the API is used to download and upload changes.
 */
class TicketRepository(
    private val api: PanalSuiteApi,
    private val local: TicketLocalDataSource
) {

    val tickets: Flow<List<Ticket>> = local.tickets.map { list ->
        list.mapNotNull { it.toDomain() }.sortedByDescending { it.updatedAt }
    }

    fun ticket(ticketId: String): Flow<Ticket?> =
        tickets.map { list -> list.firstOrNull { it.id == ticketId } }

    /**
     * Downloads the tickets from the API and merges them with the local copy.
     * Local changes that are newer than the server version are uploaded first.
     */
    suspend fun refresh(): SyncResult {
        val result = runRemote {
            val merged = api.getTickets()
                .filter { it.id != null }
                .associateBy { it.id.orEmpty() }
                .toMutableMap()

            for (localTicket in local.current()) {
                val ticketId = localTicket.id ?: continue
                val remoteTicket = merged[ticketId]
                if (remoteTicket == null) {
                    api.createTicket(localTicket)
                    merged[ticketId] = localTicket
                } else if ((localTicket.updatedAt ?: 0L) > (remoteTicket.updatedAt ?: 0L)) {
                    api.updateTicket(ticketId, localTicket)
                    merged[ticketId] = localTicket
                }
            }
            local.replaceAll(merged.values.sortedByDescending { it.updatedAt ?: 0L })
        }
        if (result == SyncResult.OFFLINE && local.current().isEmpty()) {
            local.replaceAll(SeedTickets.all)
        }
        return result
    }

    suspend fun createTicket(draft: TicketDraft, requester: User): CreateResult {
        val now = System.currentTimeMillis()
        val ticketId = "PS-${now.toString().takeLast(6)}"
        val ticket = TicketDto(
            id = ticketId,
            title = draft.title.trim(),
            description = draft.description.trim(),
            category = draft.category.name,
            priority = draft.priority.name,
            status = TicketStatus.OPEN.name,
            branch = draft.branch,
            requesterEmail = requester.email,
            requesterName = requester.name,
            resolverTeam = draft.category.team,
            createdAt = now,
            updatedAt = now,
            history = listOf(HistoryDto(null, TicketStatus.OPEN.name, requester.name, now))
        )
        local.update { list -> listOf(ticket) + list }
        val result = runRemote { api.createTicket(ticket) }
        return CreateResult(ticketId, result)
    }

    /** Applies a workflow action (take, resolve, close, reopen) and records it in the history. */
    suspend fun applyAction(ticketId: String, action: TicketAction, user: User): SyncResult {
        val now = System.currentTimeMillis()
        var updatedTicket: TicketDto? = null

        local.update { list ->
            list.map { ticket ->
                if (ticket.id != ticketId) return@map ticket
                val target = action.targetStatus
                val changed = ticket.copy(
                    status = target.name,
                    assigneeEmail = when (action) {
                        TicketAction.TAKE -> user.email
                        TicketAction.REOPEN -> null
                        else -> ticket.assigneeEmail
                    },
                    assigneeName = when (action) {
                        TicketAction.TAKE -> user.name
                        TicketAction.REOPEN -> null
                        else -> ticket.assigneeName
                    },
                    updatedAt = now,
                    history = ticket.history.orEmpty() + HistoryDto(ticket.status, target.name, user.name, now)
                )
                updatedTicket = changed
                changed
            }
        }

        val ticketToUpload = updatedTicket ?: return SyncResult.OFFLINE
        return runRemote { api.updateTicket(ticketId, ticketToUpload) }
    }

    private suspend fun runRemote(block: suspend () -> Unit): SyncResult =
        try {
            block()
            SyncResult.SYNCED
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // No connection, server down or unexpected response: the local copy is kept.
            SyncResult.OFFLINE
        }
}
