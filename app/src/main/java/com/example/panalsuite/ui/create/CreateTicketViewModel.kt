package com.example.panalsuite.ui.create

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.panalsuite.PanalSuiteApp
import com.example.panalsuite.R
import com.example.panalsuite.data.preferences.SessionManager
import com.example.panalsuite.data.repository.SyncResult
import com.example.panalsuite.data.repository.TicketDraft
import com.example.panalsuite.data.repository.TicketRepository
import com.example.panalsuite.model.Category
import com.example.panalsuite.model.Priority
import com.example.panalsuite.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateTicketUiState(
    val title: String = "",
    val description: String = "",
    val category: Category? = null,
    val priority: Priority = Priority.MEDIUM,
    val branch: String? = null,
    @param:StringRes val titleError: Int? = null,
    @param:StringRes val descriptionError: Int? = null,
    @param:StringRes val categoryError: Int? = null,
    @param:StringRes val branchError: Int? = null,
    val isSaving: Boolean = false,
    val createdTicketId: String? = null,
    @param:StringRes val resultMessage: Int? = null
)

class CreateTicketViewModel(
    private val ticketRepository: TicketRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTicketUiState())
    val uiState: StateFlow<CreateTicketUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        val value = title.take(Validators.TITLE_MAX_LENGTH)
        _uiState.update {
            it.copy(title = value, titleError = if (it.titleError != null) Validators.validateTitle(value) else null)
        }
    }

    fun onDescriptionChange(description: String) {
        val value = description.take(Validators.DESCRIPTION_MAX_LENGTH)
        _uiState.update {
            it.copy(
                description = value,
                descriptionError = if (it.descriptionError != null) Validators.validateDescription(value) else null
            )
        }
    }

    fun onCategorySelected(category: Category) {
        _uiState.update { it.copy(category = category, categoryError = null) }
    }

    fun onPrioritySelected(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onBranchSelected(branch: String) {
        _uiState.update { it.copy(branch = branch, branchError = null) }
    }

    fun submit() {
        val state = _uiState.value
        val titleError = Validators.validateTitle(state.title)
        val descriptionError = Validators.validateDescription(state.description)
        val categoryError = Validators.validateSelection(state.category, R.string.error_category_required)
        val branchError = Validators.validateSelection(state.branch, R.string.error_branch_required)

        _uiState.update {
            it.copy(
                titleError = titleError,
                descriptionError = descriptionError,
                categoryError = categoryError,
                branchError = branchError
            )
        }

        val category = state.category
        val branch = state.branch
        if (titleError != null || descriptionError != null || category == null || branch == null) return

        viewModelScope.launch {
            val user = sessionManager.currentUser.first() ?: return@launch
            _uiState.update { it.copy(isSaving = true) }
            val result = ticketRepository.createTicket(
                draft = TicketDraft(state.title, state.description, category, state.priority, branch),
                requester = user
            )
            _uiState.update {
                it.copy(
                    isSaving = false,
                    createdTicketId = result.ticketId,
                    resultMessage = if (result.syncResult == SyncResult.SYNCED) {
                        R.string.msg_ticket_created
                    } else {
                        R.string.msg_ticket_saved_offline
                    }
                )
            }
        }
    }

    /** Called after navigating to the new ticket so the form starts clean next time. */
    fun onNavigated() {
        _uiState.value = CreateTicketUiState()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PanalSuiteApp
                CreateTicketViewModel(app.container.ticketRepository, app.container.sessionManager)
            }
        }
    }
}
