package com.example.panalsuite.ui.inbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panalsuite.R
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.model.User
import com.example.panalsuite.ui.components.EmptyState
import com.example.panalsuite.ui.components.GradientHeader
import com.example.panalsuite.ui.components.OfflineBanner
import com.example.panalsuite.ui.components.TicketCard
import com.example.panalsuite.ui.theme.Honey200
import com.example.panalsuite.ui.theme.color

@Composable
fun InboxScreen(
    onTicketClick: (String) -> Unit,
    viewModel: InboxViewModel = viewModel(factory = InboxViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    InboxContent(
        uiState = uiState,
        onTicketClick = onTicketClick,
        onStatusSelected = viewModel::onStatusSelected,
        onQueryChange = viewModel::onQueryChange,
        onRefresh = viewModel::refresh
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InboxContent(
    uiState: InboxUiState,
    onTicketClick: (String) -> Unit,
    onStatusSelected: (TicketStatus?) -> Unit,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        InboxHeader(
            user = uiState.user,
            ticketCount = uiState.totalVisible,
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh
        )

        AnimatedVisibility(visible = uiState.isOffline) {
            OfflineBanner(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp))
        }

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.inbox_search_hint)) },
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        )

        StatusFilterRow(
            selected = uiState.selectedStatus,
            total = uiState.totalVisible,
            countByStatus = uiState.countByStatus,
            onStatusSelected = onStatusSelected
        )

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isLoaded && uiState.tickets.isEmpty()) {
                    item {
                        EmptyState(
                            title = stringResource(R.string.inbox_empty_title),
                            message = stringResource(R.string.inbox_empty_message)
                        )
                    }
                }
                items(uiState.tickets, key = { it.id }) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun InboxHeader(
    user: User?,
    ticketCount: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val rotation = if (isRefreshing) {
        val transition = rememberInfiniteTransition(label = "refresh")
        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
            label = "refreshAngle"
        )
        angle
    } else {
        0f
    }

    GradientHeader {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.inbox_greeting, user?.name?.substringBefore(" ").orEmpty()),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    if (user != null) {
                        Text(
                            text = stringResource(user.role.labelRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Honey200
                        )
                    }
                }
                IconButton(onClick = onRefresh, enabled = !isRefreshing) {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = stringResource(R.string.cd_refresh),
                        tint = Honey200,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.inbox_ticket_count, ticketCount),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun StatusFilterRow(
    selected: TicketStatus?,
    total: Int,
    countByStatus: Map<TicketStatus, Int>,
    onStatusSelected: (TicketStatus?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onStatusSelected(null) },
                label = { Text(stringResource(R.string.filter_all, total)) }
            )
        }
        items(TicketStatus.entries) { status ->
            FilterChip(
                selected = selected == status,
                onClick = { onStatusSelected(if (selected == status) null else status) },
                label = {
                    Text(stringResource(R.string.filter_status_count, stringResource(status.labelRes), countByStatus[status] ?: 0))
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(status.color())
                    )
                }
            )
        }
    }
}
