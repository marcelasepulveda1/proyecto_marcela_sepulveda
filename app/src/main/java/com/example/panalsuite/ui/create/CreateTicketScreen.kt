package com.example.panalsuite.ui.create

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panalsuite.R
import com.example.panalsuite.model.Branches
import com.example.panalsuite.model.Category
import com.example.panalsuite.model.Priority
import com.example.panalsuite.ui.components.SectionTitle
import com.example.panalsuite.ui.theme.PanalSuiteTheme
import com.example.panalsuite.ui.theme.color
import com.example.panalsuite.util.Validators

@Composable
fun CreateTicketScreen(
    onTicketCreated: (String) -> Unit,
    viewModel: CreateTicketViewModel = viewModel(factory = CreateTicketViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.createdTicketId) {
        val ticketId = uiState.createdTicketId ?: return@LaunchedEffect
        uiState.resultMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        onTicketCreated(ticketId)
        viewModel.onNavigated()
    }

    CreateTicketContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategorySelected = viewModel::onCategorySelected,
        onPrioritySelected = viewModel::onPrioritySelected,
        onBranchSelected = viewModel::onBranchSelected,
        onSubmit = viewModel::submit
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CreateTicketContent(
    uiState: CreateTicketUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onPrioritySelected: (Priority) -> Unit,
    onBranchSelected: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.create_title)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = stringResource(R.string.create_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.label_title)) },
                singleLine = true,
                isError = uiState.titleError != null,
                supportingText = {
                    FieldSupport(
                        errorRes = uiState.titleError,
                        length = uiState.title.length,
                        max = Validators.TITLE_MAX_LENGTH
                    )
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.label_description)) },
                minLines = 4,
                isError = uiState.descriptionError != null,
                supportingText = {
                    FieldSupport(
                        errorRes = uiState.descriptionError,
                        length = uiState.description.length,
                        max = Validators.DESCRIPTION_MAX_LENGTH
                    )
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(stringResource(R.string.label_category))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Category.entries.forEach { category ->
                        FilterChip(
                            selected = uiState.category == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(stringResource(category.labelRes)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(category.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
                ErrorText(uiState.categoryError)
                AnimatedVisibility(visible = uiState.category != null) {
                    Text(
                        text = stringResource(R.string.create_team_hint, uiState.category?.team.orEmpty()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(stringResource(R.string.label_priority))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { priority ->
                        PriorityOption(
                            priority = priority,
                            selected = uiState.priority == priority,
                            onClick = { onPrioritySelected(priority) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(stringResource(R.string.label_branch))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Branches.all.forEach { branch ->
                        FilterChip(
                            selected = uiState.branch == branch,
                            onClick = { onBranchSelected(branch) },
                            label = { Text(branch) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_place),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
                ErrorText(uiState.branchError)
            }

            Button(
                onClick = onSubmit,
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.action_create_ticket))
                }
            }
        }
    }
}

/** Shows the validation error, or the character counter when the field is valid. */
@Composable
private fun FieldSupport(@StringRes errorRes: Int?, length: Int, max: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = errorRes?.let { stringResource(it) }.orEmpty(),
            modifier = Modifier.weight(1f)
        )
        Text(text = stringResource(R.string.char_counter, length, max))
    }
}

@Composable
private fun ErrorText(@StringRes errorRes: Int?) {
    AnimatedVisibility(visible = errorRes != null) {
        Text(
            text = errorRes?.let { stringResource(it) }.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

/** Priority selector: the selected option is filled with the priority color. */
@Composable
private fun PriorityOption(
    priority: Priority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = if (selected) priority.color() else MaterialTheme.colorScheme.surfaceContainerLowest,
        label = "priorityBackground"
    )
    val content = if (selected) Color.White else priority.color()
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = background,
        contentColor = content,
        border = BorderStroke(1.5.dp, priority.color())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(priority.labelRes),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun CreateTicketPreview() {
    PanalSuiteTheme {
        CreateTicketContent(
            uiState = CreateTicketUiState(
                title = "Caja",
                titleError = R.string.error_title_short,
                category = Category.STORE,
                priority = Priority.HIGH
            ),
            onTitleChange = {},
            onDescriptionChange = {},
            onCategorySelected = {},
            onPrioritySelected = {},
            onBranchSelected = {},
            onSubmit = {}
        )
    }
}
