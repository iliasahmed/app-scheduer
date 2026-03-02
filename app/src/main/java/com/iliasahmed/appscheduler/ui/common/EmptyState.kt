package com.iliasahmed.appscheduler.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iliasahmed.appscheduler.ui.scheduleList.ScheduleViewModel

@Composable
fun EmptyState(
    tab: ScheduleViewModel.ScheduleTab,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSearching) {
                "No matching schedules"
            } else when (tab) {
                ScheduleViewModel.ScheduleTab.PENDING -> "No pending schedules"
                ScheduleViewModel.ScheduleTab.CANCELLED -> "No cancelled schedules"
                ScheduleViewModel.ScheduleTab.COMPLETED -> "No completed schedules"
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isSearching) {
                "Try a different search term"
            } else when (tab) {
                ScheduleViewModel.ScheduleTab.PENDING -> "Tap + to schedule your first app launch"
                ScheduleViewModel.ScheduleTab.CANCELLED -> "Cancelled schedules will appear here"
                ScheduleViewModel.ScheduleTab.COMPLETED -> "Completed schedules will appear here"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}