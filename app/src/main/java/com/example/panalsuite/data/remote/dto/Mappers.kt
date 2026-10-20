package com.example.panalsuite.data.remote.dto

import com.example.panalsuite.model.Category
import com.example.panalsuite.model.HistoryEntry
import com.example.panalsuite.model.Priority
import com.example.panalsuite.model.Role
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User

/** Converts the API/cache representation into the domain model. Returns null if required data is missing. */
fun TicketDto.toDomain(): Ticket? {
    val ticketId = id ?: return null
    val ticketTitle = title ?: return null
    val created = createdAt ?: 0L
    return Ticket(
        id = ticketId,
        title = ticketTitle,
        description = description.orEmpty(),
        category = enumOrDefault(category, Category.INTERNAL_SUPPORT),
        priority = enumOrDefault(priority, Priority.MEDIUM),
        status = enumOrDefault(status, TicketStatus.OPEN),
        branch = branch.orEmpty(),
        requesterEmail = requesterEmail.orEmpty(),
        requesterName = requesterName.orEmpty(),
        resolverTeam = resolverTeam.orEmpty(),
        assigneeEmail = assigneeEmail,
        assigneeName = assigneeName,
        createdAt = created,
        updatedAt = updatedAt ?: created,
        history = history.orEmpty().mapNotNull { it.toDomain() }
    )
}

fun HistoryDto.toDomain(): HistoryEntry? {
    val target = toStatus?.let { enumOrNull<TicketStatus>(it) } ?: return null
    return HistoryEntry(
        fromStatus = fromStatus?.let { enumOrNull<TicketStatus>(it) },
        toStatus = target,
        changedBy = changedBy.orEmpty(),
        changedAt = changedAt ?: 0L
    )
}

fun UserDto.toDomain(): User? {
    val userEmail = email ?: return null
    return User(
        email = userEmail,
        name = name.orEmpty(),
        role = enumOrDefault(role, Role.REQUESTER)
    )
}

private inline fun <reified T : Enum<T>> enumOrNull(name: String): T? =
    enumValues<T>().firstOrNull { it.name == name }

private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T =
    name?.let { enumOrNull<T>(it) } ?: default
