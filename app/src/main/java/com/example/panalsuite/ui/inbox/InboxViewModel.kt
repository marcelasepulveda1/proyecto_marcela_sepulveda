package com.example.panalsuite.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.panalsuite.PanalSuiteApp
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.data.repository.SyncResult
import com.example.panalsuite.data.repository.TicketRepository
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.TicketWorkflow
import com.example.panalsuite.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InboxUiState(
    val user: User? = null,
    val tickets: List<Ticket> = emptyList(),
    val totalVisible: Int = 0,
    val countByStatus: Map<TicketStatus, Int> = emptyMap(),
    val selectedStatus: TicketStatus? = null,
    val query: String = "",
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val isLoaded: Boolean = false
)

private data class SyncState(val isRefreshing: Boolean = false, val isOffline: Boolean = false)

class InboxViewModel(
    private val ticketRepository: TicketRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val selectedStatus = MutableStateFlow<TicketStatus?>(null)
    private val query = MutableStateFlow("")
    private val syncState = MutableStateFlow(SyncState())

    val uiState: StateFlow<InboxUiState> = combine(
        sessionManager.currentUser,
        ticketRepository.tickets,
        selectedStatus,
        query,
        syncState
    ) { user, tickets, status, text, sync ->
        // 1. Only the tickets this role is allowed to see.
        val visible = if (user == null) emptyList() else tickets.filter { TicketWorkflow.isVisibleTo(it, user) }
        // 2. Status filter + text search.
        val filtered = visible
            .filter { status == null || it.status == status }
            .filter { it.matches(text) }

        InboxUiState(
            user = user,
            tickets = filtered,
            totalVisible = visible.size,
            countByStatus = visible.groupingBy { it.status }.eachCount(),
            selectedStatus = status,
            query = text,
            isRefreshing = sync.isRefreshing,
            isOffline = sync.isOffline,
            isLoaded = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InboxUiState())

    init {
        refresh()
    }

    fun refresh() {
        if (syncState.value.isRefreshing) return
        viewModelScope.launch {
            syncState.update { it.copy(isRefreshing = true) }
            val result = ticketRepository.refresh()
            syncState.value = SyncState(isRefreshing = false, isOffline = result == SyncResult.OFFLINE)
        }
    }

    fun onStatusSelected(status: TicketStatus?) {
        selectedStatus.value = status
    }

    fun onQueryChange(text: String) {
        query.value = text
    }

    private fun Ticket.matches(text: String): Boolean {
        if (text.isBlank()) return true
        val term = text.trim()
        return title.contains(term, ignoreCase = true) ||
            id.contains(term, ignoreCase = true) ||
            branch.contains(term, ignoreCase = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PanalSuiteApp
                InboxViewModel(app.container.ticketRepository, app.container.sessionManager)
            }
        }
    }
}
