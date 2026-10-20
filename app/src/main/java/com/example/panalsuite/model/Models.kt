package com.example.panalsuite.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.panalsuite.R

enum class Role(@param:StringRes val labelRes: Int) {
    REQUESTER(R.string.role_requester),
    RESOLVER(R.string.role_resolver),
    SUPERVISOR(R.string.role_supervisor)
}

enum class TicketStatus(@param:StringRes val labelRes: Int) {
    OPEN(R.string.status_open),
    IN_PROGRESS(R.string.status_in_progress),
    RESOLVED(R.string.status_resolved),
    CLOSED(R.string.status_closed);

    /** Next status in the workflow, or null when the ticket is already closed. */
    fun next(): TicketStatus? = when (this) {
        OPEN -> IN_PROGRESS
        IN_PROGRESS -> RESOLVED
        RESOLVED -> CLOSED
        CLOSED -> null
    }
}

enum class Priority(@param:StringRes val labelRes: Int) {
    LOW(R.string.priority_low),
    MEDIUM(R.string.priority_medium),
    HIGH(R.string.priority_high),
    CRITICAL(R.string.priority_critical)
}

/** Each category is routed to the team that resolves that kind of problem. */
enum class Category(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
    val team: String
) {
    STORE(R.string.category_store, R.drawable.ic_category_store, "Equipo Tiendas"),
    PHARMACY(R.string.category_pharmacy, R.drawable.ic_category_pharmacy, "Equipo Farmacia"),
    SUPPLIER(R.string.category_supplier, R.drawable.ic_category_supplier, "Compras y Abastecimiento"),
    INFRASTRUCTURE(R.string.category_infrastructure, R.drawable.ic_category_infrastructure, "Mantención"),
    INTERNAL_SUPPORT(R.string.category_internal_support, R.drawable.ic_category_support, "Mesa de Ayuda TI")
}

/** Branches (sucursales) where a ticket can be reported. */
object Branches {
    val all = listOf(
        "Sucursal Alameda",
        "Sucursal Providencia",
        "Sucursal Maipú",
        "Sucursal Ñuñoa",
        "Bodega Central"
    )
}

data class User(
    val email: String,
    val name: String,
    val role: Role
)

data class Comment(
    val id: String,
    val authorName: String,
    val message: String,
    val createdAt: Long
)

data class HistoryEntry(
    val fromStatus: TicketStatus?,
    val toStatus: TicketStatus,
    val changedBy: String,
    val changedAt: Long
)

data class Ticket(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val status: TicketStatus,
    val branch: String,
    val requesterEmail: String,
    val requesterName: String,
    val resolverTeam: String,
    val assigneeEmail: String?,
    val assigneeName: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val comments: List<Comment> = emptyList(),
    val evidenceUris: List<String> = emptyList(),
    val history: List<HistoryEntry> = emptyList()
)
