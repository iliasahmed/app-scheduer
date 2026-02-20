package com.iliasahmed.appscheduler.ui.scheduleList

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.iliasahmed.appscheduler.domain.model.ScheduleModel

const val scheduleListScreenRoute = "scheduleListScreenRoute"

fun NavGraphBuilder.scheduleListScreen(
    onItemClick: (ScheduleModel) -> Unit
) {
    composable(route = scheduleListScreenRoute) {
        ScheduleListRoute(
            onScheduleItemClick = onItemClick,
        )
    }
}