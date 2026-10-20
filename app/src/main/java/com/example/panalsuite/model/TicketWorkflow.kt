package com.example.panalsuite.model

import androidx.annotation.StringRes
import com.example.panalsuite.R

/** Actions that move a ticket through its workflow. */
enum class TicketAction(
    @param:StringRes val labelRes: Int,
    val targetStatus: TicketStatus
) {
    TAKE(R.string.action_take, TicketStatus.IN_PROGRESS),
    RESOLVE(R.string.action_resolve, TicketStatus.RESOLVED),
    CLOSE(R.string.action_close, TicketStatus.CLOSED),
    REOPEN(R.string.action_reopen, TicketStatus.OPEN)
}

/**
 * Business rules by role:
 * - Requester: creates tickets, sees only their own and confirms (closes) or reopens resolved ones.
 * - Resolver: sees open tickets and the ones assigned to them; takes and resolves them.
 * - Supervisor: sees everything, closes or reopens tickets and has access to the summary.
 */
object TicketWorkflow {

    fun isVisibleTo(ticket: Ticket, user: User): Boolean = when (user.role) {
        Role.REQUESTER -> ticket.requesterEmail.equals(user.email, ignoreCase = true)
        Role.RESOLVER -> ticket.status == TicketStatus.OPEN ||
            ticket.assigneeEmail.equals(user.email, ignoreCase = true)
        Role.SUPERVISOR -> true
    }

    fun actionsFor(ticket: Ticket, user: User): List<TicketAction> = when (user.role) {
        Role.REQUESTER -> {
            val isOwner = ticket.requesterEmail.equals(user.email, ignoreCase = true)
            if (isOwner && ticket.status == TicketStatus.RESOLVED) {
                listOf(TicketAction.CLOSE, TicketAction.REOPEN)
            } else {
                emptyList()
            }
        }

        Role.RESOLVER -> when {
            ticket.status == TicketStatus.OPEN -> listOf(TicketAction.TAKE)
            ticket.status == TicketStatus.IN_PROGRESS &&
                ticket.assigneeEmail.equals(user.email, ignoreCase = true) -> listOf(TicketAction.RESOLVE)
            else -> emptyList()
        }

        Role.SUPERVISOR -> when (ticket.status) {
            TicketStatus.RESOLVED -> listOf(TicketAction.CLOSE, TicketAction.REOPEN)
            TicketStatus.CLOSED -> listOf(TicketAction.REOPEN)
            else -> emptyList()
        }
    }

    fun canCreateTickets(user: User): Boolean = user.role != Role.RESOLVER

    fun canSeeSummary(user: User): Boolean = user.role == Role.SUPERVISOR
}
