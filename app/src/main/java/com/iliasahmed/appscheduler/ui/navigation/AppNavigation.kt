package com.iliasahmed.appscheduler.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.iliasahmed.appscheduler.ui.scheduleList.scheduleListScreen
import com.iliasahmed.appscheduler.ui.scheduleList.scheduleListScreenRoute

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController:
    NavHostController = rememberNavController(),
    startDestination: String = scheduleListScreenRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        scheduleListScreen(
            onItemClick = {

            }
        )
    }
}