package com.example.panalsuite.ui.detail

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.panalsuite.PanalSuiteApp
import com.example.panalsuite.R
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.data.repository.SyncResult
import com.example.panalsuite.data.repository.TicketRepository
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketAction
import com.example.panalsuite.model.TicketWorkflow
import com.example.panalsuite.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val actions: List<TicketAction> = emptyList(),
    val isLoaded: Boolean = false,
    val isUpdating: Boolean = false,
    @param:StringRes val messageRes: Int? = null
)

private data class ActionState(val isUpdating: Boolean = false, @param:StringRes val messageRes: Int? = null)

class TicketDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val ticketRepository: TicketRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val ticketId: String = checkNotNull(savedStateHandle[Screen.TicketDetail.ARG_TICKET_ID])
    private val actionState = MutableStateFlow(ActionState())

    val uiState: StateFlow<TicketDetailUiState> = combine(
        ticketRepository.ticket(ticketId),
        sessionManager.currentUser,
        actionState
    ) { ticket, user, action ->
        TicketDetailUiState(
            ticket = ticket,
            actions = if (ticket != null && user != null) TicketWorkflow.actionsFor(ticket, user) else emptyList(),
            isLoaded = true,
            isUpdating = action.isUpdating,
            messageRes = action.messageRes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TicketDetailUiState())

    fun onAction(action: TicketAction) {
        if (actionState.value.isUpdating) return
        viewModelScope.launch {
            val user = sessionManager.currentUser.first() ?: return@launch
            actionState.update { it.copy(isUpdating = true) }
            val result = ticketRepository.applyAction(ticketId, action, user)
            actionState.value = ActionState(
                isUpdating = false,
                messageRes = if (result == SyncResult.SYNCED) R.string.msg_status_updated else R.string.msg_status_saved_offline
            )
        }
    }

    fun onMessageShown() {
        actionState.update { it.copy(messageRes = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PanalSuiteApp
                TicketDetailViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    ticketRepository = app.container.ticketRepository,
                    sessionManager = app.container.sessionManager
                )
            }
        }
    }
}
