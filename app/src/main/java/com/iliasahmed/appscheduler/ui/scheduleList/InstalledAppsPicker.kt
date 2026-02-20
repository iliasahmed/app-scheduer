package com.iliasahmed.appscheduler.ui.scheduleList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.iliasahmed.appscheduler.domain.model.InstalledAppModel
import com.iliasahmed.appscheduler.ui.common.ErrorState

@Composable
fun InstalledAppsPicker(
    appsUiState: ScheduleViewModel.UiState<List<InstalledAppModel>>,
    onSelect: (InstalledAppModel) -> Unit
) {
    when (appsUiState) {
        is ScheduleViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ScheduleViewModel.UiState.Error -> {
            ErrorState(message = appsUiState.message)
        }

        is ScheduleViewModel.UiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(appsUiState.data, key = { it.packageName }) { app ->
                    ListItem(
                        headlineContent = { Text(app.appName) },
                        supportingContent = { Text(app.packageName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(app) }
                    )
                    HorizontalDivider()
                }
            }
        }

        else -> Unit
    }
}