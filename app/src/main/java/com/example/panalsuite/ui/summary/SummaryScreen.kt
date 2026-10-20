package com.example.panalsuite.ui.summary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panalsuite.R
import com.example.panalsuite.model.Category
import com.example.panalsuite.model.Priority
import com.example.panalsuite.model.TicketStatus
import com.example.panalsuite.ui.components.GradientHeader
import com.example.panalsuite.ui.components.HexagonShape
import com.example.panalsuite.ui.components.SectionTitle
import com.example.panalsuite.ui.theme.Honey200
import com.example.panalsuite.ui.theme.color

@Composable
fun SummaryScreen(viewModel: SummaryViewModel = viewModel(factory = SummaryViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        GradientHeader {
            Column {
                Text(
                    text = stringResource(R.string.summary_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.summary_subtitle, uiState.total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Honey200
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CriticalAlert(count = uiState.criticalPending)

            SectionTitle(stringResource(R.string.summary_by_status))
            TicketStatus.entries.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { status ->
                        StatusTile(
                            status = status,
                            count = uiState.byStatus[status] ?: 0,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SummaryCard(title = stringResource(R.string.summary_by_priority)) {
                Priority.entries.reversed().forEach { priority ->
                    BarRow(
                        label = stringResource(priority.labelRes),
                        count = uiState.byPriority[priority] ?: 0,
                        total = uiState.total,
                        color = priority.color()
                    )
                }
            }

            SummaryCard(title = stringResource(R.string.summary_by_category)) {
                Category.entries.forEach { category ->
                    BarRow(
                        label = stringResource(category.labelRes),
                        count = uiState.byCategory[category] ?: 0,
                        total = uiState.total,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CriticalAlert(count: Int) {
    val hasCritical = count > 0
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (hasCritical) Priority.CRITICAL.color().copy(alpha = 0.12f) else TicketStatus.RESOLVED.color().copy(alpha = 0.12f),
        contentColor = if (hasCritical) Priority.CRITICAL.color() else TicketStatus.RESOLVED.color()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(if (hasCritical) R.drawable.ic_offline else R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = if (hasCritical) {
                    stringResource(R.string.summary_critical_alert, count)
                } else {
                    stringResource(R.string.summary_no_critical)
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StatusTile(status: TicketStatus, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(HexagonShape)
                    .background(status.color().copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = status.color()
                )
            }
            Text(
                text = stringResource(status.labelRes),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SummaryCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle(title)
            content()
        }
    }
}

/** Horizontal bar that grows from 0 to its value when the screen opens. */
@Composable
private fun BarRow(label: String, count: Int, total: Int, color: Color) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val target = if (total == 0) 0f else count.toFloat() / total
    val fraction by animateFloatAsState(
        targetValue = if (started) target else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "barFraction"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text(text = count.toString(), style = MaterialTheme.typography.labelLarge)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}
