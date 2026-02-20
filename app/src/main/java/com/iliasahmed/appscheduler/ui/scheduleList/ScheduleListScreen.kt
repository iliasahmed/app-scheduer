package com.iliasahmed.appscheduler.ui.scheduleList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliasahmed.appscheduler.R
import com.iliasahmed.appscheduler.domain.model.InstalledAppModel
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.ui.common.EmptyState
import com.iliasahmed.appscheduler.ui.common.ErrorState

@Composable
internal fun ScheduleListRoute(
    viewModel: ScheduleViewModel = hiltViewModel(),
    onScheduleItemClick: (ScheduleModel) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
        viewModel.loadSchedules()
    }
    ScheduleListScreen(
        viewModel = viewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    viewModel: ScheduleViewModel,
) {
    val schedulesUiState by viewModel.schedulesUiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()
    val appsUiState by viewModel.appsUiState.collectAsStateWithLifecycle()

    var showCancelDialog by remember { mutableStateOf<ScheduleModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf<ScheduleModel?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }

    var selectedApp by remember { mutableStateOf<InstalledAppModel?>(null) }
    var showAppPicker by remember { mutableStateOf(false) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedScheduleForEdit by remember { mutableStateOf<ScheduleModel?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(actionUiState) {
        when (val op = actionUiState) {
            is ScheduleViewModel.UiState.Success -> {
                snackBarHostState.showSnackbar(op.data, actionLabel = null, withDismissAction = false,SnackbarDuration.Short)
                viewModel.resetActionState()
            }
            is ScheduleViewModel.UiState.Error -> {
                snackBarHostState.showSnackbar(op.message, actionLabel = null, withDismissAction = false,SnackbarDuration.Long)
                viewModel.resetActionState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary,
                    titleContentColor = colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAppPicker = true
                    viewModel.loadInstalledApps()
                },
                containerColor = colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.title_add_schedule))
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = schedulesUiState) {
                is ScheduleViewModel.UiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                is ScheduleViewModel.UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ScheduleViewModel.UiState.Success -> {
                    val schedules = state.data
                    if (schedules.isEmpty()) {
                        EmptyState(modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = schedules,
                                key = { it.id }
                            ) { schedule ->
                                ScheduleCard(
                                    schedule = schedule,
                                    onEditClick = {
                                        selectedScheduleForEdit = schedule
                                        showEditDialog = true
                                    },
                                    onCancelClick = { showCancelDialog = schedule },
                                    onDeleteClick = { showDeleteDialog = schedule }
                                )
                            }
                        }
                    }
                }
                is ScheduleViewModel.UiState.Idle -> {  }
            }

            if (actionUiState is ScheduleViewModel.UiState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (showAppPicker) {
                ModalBottomSheet(
                    onDismissRequest = { showAppPicker = false },
                    sheetState = sheetState
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(R.string.title_select_app),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(Modifier.height(12.dp))

                        InstalledAppsPicker(
                            appsUiState = appsUiState,
                            onSelect = { app ->
                                showAppPicker = false
                                selectedApp = app
                                showAddScheduleDialog = true
                            }
                        )
                    }
                }
            }

            if (showAddScheduleDialog && selectedApp != null) {
                ScheduleDateTimeAlertDialog(
                    title = stringResource(R.string.title_add_schedule),
                    primaryText = selectedApp!!.appName,
                    secondaryText = selectedApp!!.packageName,
                    confirmText = stringResource(R.string.action_save),
                    initialDateTime = null,
                    actionUiState = actionUiState,
                    onConfirm = { date ->
                        viewModel.scheduleApp(selectedApp!!.appName, selectedApp!!.packageName, date)
                    },
                    onDismiss = {
                        showAddScheduleDialog = false
                        selectedApp = null
                    }
                )
            }

            if (showEditDialog || selectedScheduleForEdit != null) {
                ScheduleDateTimeAlertDialog(
                    title = stringResource(R.string.title_edit_schedule),
                    primaryText = selectedScheduleForEdit!!.appName,
                    secondaryText = selectedScheduleForEdit!!.packageName,
                    confirmText = stringResource(R.string.action_update),
                    initialDateTime = selectedScheduleForEdit!!.scheduledTime,
                    actionUiState = actionUiState,
                    onConfirm = { newDate ->
                        viewModel.rescheduleApp(selectedScheduleForEdit!!, newDate)
                    },
                    onDismiss = {
                        showEditDialog = false
                        selectedScheduleForEdit = null
                    }
                )
            }
        }
    }

    showCancelDialog?.let { schedule ->
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text(stringResource(R.string.title_cancel_schedule)) },
            text = { Text(stringResource(R.string.msg_cancel_schedule_confirm, schedule.appName)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelSchedule(schedule)
                        showCancelDialog = null
                    }
                ) {
                    Text(stringResource(R.string.btn_yes_cancel), color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) { Text(stringResource(R.string.btn_keep)) }
            }
        )
    }

    showDeleteDialog?.let { schedule ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.title_delete_schedule)) },
            text = {
                Text(
                    stringResource(
                        R.string.msg_delete_schedule_confirm,
                        schedule.appName
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSchedule(schedule)
                        showDeleteDialog = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete), color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}


