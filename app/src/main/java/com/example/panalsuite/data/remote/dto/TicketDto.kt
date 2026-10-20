package com.example.panalsuite.data.remote.dto

/**
 * Ticket as it travels through the REST API and as it is cached on the phone.
 * Every field is nullable because the JSON may come incomplete; the mapper decides the defaults.
 */
data class TicketDto(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val branch: String? = null,
    val requesterEmail: String? = null,
    val requesterName: String? = null,
    val resolverTeam: String? = null,
    val assigneeEmail: String? = null,
    val assigneeName: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val history: List<HistoryDto>? = null
)

data class HistoryDto(
    val fromStatus: String? = null,
    val toStatus: String? = null,
    val changedBy: String? = null,
    val changedAt: Long? = null
)

data class UserDto(
    val email: String? = null,
    val name: String? = null,
    val role: String? = null
)
