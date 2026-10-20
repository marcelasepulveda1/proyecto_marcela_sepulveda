package com.example.panalsuite.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.panalsuite.PanalSuiteApp
import com.example.panalsuite.data.repository.TicketRepository
import com.example.panalsuite.model.Category
import com.example.panalsuite.model.Priority
import com.example.panalsuite.model.TicketStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SummaryUiState(
    val total: Int = 0,
    val byStatus: Map<TicketStatus, Int> = emptyMap(),
    val byPriority: Map<Priority, Int> = emptyMap(),
    val byCategory: Map<Category, Int> = emptyMap(),
    val criticalPending: Int = 0
)

/** Supervisor dashboard: counts calculated from the local copy of the tickets. */
class SummaryViewModel(ticketRepository: TicketRepository) : ViewModel() {

    val uiState: StateFlow<SummaryUiState> = ticketRepository.tickets
        .map { tickets ->
            SummaryUiState(
                total = tickets.size,
                byStatus = tickets.groupingBy { it.status }.eachCount(),
                byPriority = tickets.groupingBy { it.priority }.eachCount(),
                byCategory = tickets.groupingBy { it.category }.eachCount(),
                criticalPending = tickets.count {
                    it.priority == Priority.CRITICAL && it.status != TicketStatus.CLOSED
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SummaryUiState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PanalSuiteApp
                SummaryViewModel(app.container.ticketRepository)
            }
        }
    }
}
