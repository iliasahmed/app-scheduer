package com.iliasahmed.appscheduler.ui.scheduleList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.model.ScheduleStatus
import com.iliasahmed.appscheduler.ui.theme.StatusCancelled
import com.iliasahmed.appscheduler.ui.theme.StatusCancelledText
import com.iliasahmed.appscheduler.ui.theme.StatusCompleted
import com.iliasahmed.appscheduler.ui.theme.StatusCompletedText
import com.iliasahmed.appscheduler.ui.theme.StatusFailed
import com.iliasahmed.appscheduler.ui.theme.StatusFailedText
import com.iliasahmed.appscheduler.ui.theme.StatusPending
import com.iliasahmed.appscheduler.ui.theme.StatusPendingText

@Composable
fun ScheduleCard(
    schedule: ScheduleModel,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = schedule.appName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = schedule.scheduledTime.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusChip(status = schedule.status)
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (schedule.isEditable()) {
                    TextButton(
                        onClick = onEditClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                }

                if (schedule.isCancellable()) {
                    TextButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel")
                    }
                }

                TextButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    colors = textButtonColors(
                        contentColor = colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: ScheduleStatus) {
    val backgroundColor: Color
    val textColor: Color
    val label: String

    when (status) {
        ScheduleStatus.PENDING -> {
            backgroundColor = StatusPending
            textColor = StatusPendingText
            label = "Pending"
        }

        ScheduleStatus.COMPLETED -> {
            backgroundColor = StatusCompleted
            textColor = StatusCompletedText
            label = "Completed"
        }

        ScheduleStatus.CANCELLED -> {
            backgroundColor = StatusCancelled
            textColor = StatusCancelledText
            label = "Cancelled"
        }

        ScheduleStatus.FAILED -> {
            backgroundColor = StatusFailed
            textColor = StatusFailedText
            label = "Failed"
        }
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}