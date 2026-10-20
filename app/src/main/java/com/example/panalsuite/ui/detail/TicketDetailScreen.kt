package com.example.panalsuite.ui.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panalsuite.R
import com.example.panalsuite.model.HistoryEntry
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.TicketAction
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.ui.components.CategoryIcon
import com.example.panalsuite.ui.components.HexagonShape
import com.example.panalsuite.ui.components.PriorityBadge
import com.example.panalsuite.ui.components.SectionTitle
import com.example.panalsuite.ui.components.StatusChip
import com.example.panalsuite.ui.theme.color
import com.example.panalsuite.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    onBack: () -> Unit,
    viewModel: TicketDetailViewModel = viewModel(factory = TicketDetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val message = uiState.messageRes?.let { stringResource(it) }

    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.ticket?.id.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.ticket != null) {
                ActionBar(
                    actions = uiState.actions,
                    isUpdating = uiState.isUpdating,
                    onAction = viewModel::onAction
                )
            }
        }
    ) { innerPadding ->
        val ticket = uiState.ticket
        when {
            ticket != null -> DetailContent(ticket = ticket, modifier = Modifier.padding(innerPadding))
            uiState.isLoaded -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.detail_not_found))
            }
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun DetailContent(ticket: Ticket, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIcon(category = ticket.category, size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(ticket.category.labelRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = ticket.branch,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(status = ticket.status)
            }
            Text(text = ticket.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DetailCard {
            SectionTitle(stringResource(R.string.detail_progress))
            StatusProgress(current = ticket.status)
        }

        DetailCard {
            SectionTitle(stringResource(R.string.detail_information))
            InfoRow(label = stringResource(R.string.label_priority)) { PriorityBadge(ticket.priority) }
            InfoRow(stringResource(R.string.detail_team), ticket.resolverTeam)
            InfoRow(stringResource(R.string.detail_requester), ticket.requesterName)
            InfoRow(
                stringResource(R.string.detail_assignee),
                ticket.assigneeName ?: stringResource(R.string.ticket_unassigned)
            )
            InfoRow(stringResource(R.string.detail_created), DateFormatter.full(ticket.createdAt))
            InfoRow(stringResource(R.string.detail_updated), DateFormatter.full(ticket.updatedAt))
        }

        DetailCard {
            SectionTitle(stringResource(R.string.detail_history))
            val entries = ticket.history.sortedByDescending { it.changedAt }
            entries.forEachIndexed { index, entry ->
                HistoryItem(entry = entry, isLast = index == entries.lastIndex)
            }
        }
    }
}

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

/** Four hexagon steps; reached steps take the color of the current status (animated). */
@Composable
private fun StatusProgress(current: TicketStatus) {
    val steps = TicketStatus.entries
    val currentIndex = steps.indexOf(current)
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, status ->
            val reached = index <= currentIndex
            val stepColor by animateColorAsState(
                targetValue = if (reached) current.color() else MaterialTheme.colorScheme.outlineVariant,
                label = "step$index"
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(HexagonShape)
                        .background(stepColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (reached) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = stringResource(status.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (reached) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    InfoRow(label = label) {
        Text(text = value, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
    }
}

@Composable
private fun InfoRow(label: String, value: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        value()
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

/** Timeline entry: colored dot + vertical line + description of the change. */
@Composable
private fun HistoryItem(entry: HistoryEntry, isLast: Boolean) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(entry.toStatus.color())
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 8.dp)) {
            val title = entry.fromStatus?.let { from ->
                stringResource(R.string.detail_history_change, stringResource(from.labelRes), stringResource(entry.toStatus.labelRes))
            } ?: stringResource(R.string.detail_history_created)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(R.string.detail_history_meta, entry.changedBy, DateFormatter.full(entry.changedAt)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Buttons available for the current role. The first action is the main one. */
@Composable
private fun ActionBar(
    actions: List<TicketAction>,
    isUpdating: Boolean,
    onAction: (TicketAction) -> Unit
) {
    Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surfaceContainerLowest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            if (actions.isEmpty()) {
                Text(
                    text = stringResource(R.string.detail_no_actions),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    actions.forEachIndexed { index, action ->
                        val buttonModifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                        if (index == 0) {
                            Button(
                                onClick = { onAction(action) },
                                enabled = !isUpdating,
                                shape = RoundedCornerShape(16.dp),
                                modifier = buttonModifier
                            ) {
                                if (isUpdating) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text(stringResource(action.labelRes), textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onAction(action) },
                                enabled = !isUpdating,
                                shape = RoundedCornerShape(16.dp),
                                modifier = buttonModifier
                            ) {
                                Text(stringResource(action.labelRes), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
