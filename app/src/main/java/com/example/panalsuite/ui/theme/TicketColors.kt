package com.example.panalsuite.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.panalsuite.model.Priority
import com.example.panalsuite.model.TicketStatus

/** Color that identifies each status in chips, the progress bar and the summary. */
fun TicketStatus.color(): Color = when (this) {
    TicketStatus.OPEN -> StatusOpen
    TicketStatus.IN_PROGRESS -> StatusInProgress
    TicketStatus.RESOLVED -> StatusResolved
    TicketStatus.CLOSED -> StatusClosed
}

/** Color of the side strip of each card: from grey (low) to red (critical). */
fun Priority.color(): Color = when (this) {
    Priority.LOW -> PriorityLow
    Priority.MEDIUM -> PriorityMedium
    Priority.HIGH -> PriorityHigh
    Priority.CRITICAL -> PriorityCritical
}
