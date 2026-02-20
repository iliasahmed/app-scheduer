package com.iliasahmed.appscheduler.ui.scheduleList

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iliasahmed.appscheduler.R
import com.iliasahmed.appscheduler.ui.theme.Black30
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleDateTimeAlertDialog(
    title: String,
    primaryText: String,
    secondaryText: String? = null,
    confirmText: String = stringResource(R.string.action_save),
    initialDateTime: Date? = null,
    actionUiState: ScheduleViewModel.UiState<String>,
    onConfirm: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    var pickedDateTime by remember { mutableStateOf(initialDateTime) }

    LaunchedEffect(actionUiState) {
        if (actionUiState is ScheduleViewModel.UiState.Success) onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = primaryText, style = MaterialTheme.typography.titleMedium)
                secondaryText?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }

                val formatted = pickedDateTime?.let {
                    SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(it)
                } ?: "No time selected"

                OutlinedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Selected time", style = MaterialTheme.typography.labelMedium, color = Black30)
                        Spacer(Modifier.height(6.dp))
                        Text(formatted)
                    }
                }

                Button(
                    onClick = {
                        val now = Calendar.getInstance()

                        val base = pickedDateTime ?: now.time
                        cal.time = base

                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, month)
                                cal.set(Calendar.DAY_OF_MONTH, day)

                                val isToday = year == now.get(Calendar.YEAR) &&
                                        month == now.get(Calendar.MONTH) &&
                                        day == now.get(Calendar.DAY_OF_MONTH)

                                val timePicker = TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        cal.set(Calendar.HOUR_OF_DAY, hour)
                                        cal.set(Calendar.MINUTE, minute)
                                        cal.set(Calendar.SECOND, 0)
                                        cal.set(Calendar.MILLISECOND, 0)

                                        val picked = cal.time

                                        if (picked.before(Date())) {
                                            Toast.makeText(context, "Please pick a future time", Toast.LENGTH_SHORT).show()
                                        } else {
                                            pickedDateTime = picked
                                        }
                                    },
                                    if (isToday) now.get(Calendar.HOUR_OF_DAY) else 12,
                                    if (isToday) now.get(Calendar.MINUTE) else 0,
                                    false
                                )

                                timePicker.show()
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.datePicker.minDate = now.timeInMillis
                        datePicker.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick date & time")
                }

                when (actionUiState) {
                    is ScheduleViewModel.UiState.Loading -> {
                        CircularProgressIndicator(Modifier.size(18.dp))
                    }
                    is ScheduleViewModel.UiState.Error -> {
                        Text(actionUiState.message, color = MaterialTheme.colorScheme.error)
                    }
                    else -> Unit
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = pickedDateTime != null && actionUiState !is ScheduleViewModel.UiState.Loading,
                onClick = { pickedDateTime?.let(onConfirm) }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

