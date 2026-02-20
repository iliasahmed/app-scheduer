package com.iliasahmed.appscheduler.ui.scheduleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iliasahmed.appscheduler.domain.model.InstalledAppModel
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.usecase.CancelScheduleUseCase
import com.iliasahmed.appscheduler.domain.usecase.DeleteScheduleUseCase
import com.iliasahmed.appscheduler.domain.usecase.GetInstalledAppsUseCase
import com.iliasahmed.appscheduler.domain.usecase.GetSchedulesUseCase
import com.iliasahmed.appscheduler.domain.usecase.RescheduleAppUseCase
import com.iliasahmed.appscheduler.domain.usecase.ScheduleAppUseCase
import com.iliasahmed.appscheduler.domain.utils.NoParams
import com.iliasahmed.appscheduler.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getSchedulesUseCase: GetSchedulesUseCase,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val scheduleAppUseCase: ScheduleAppUseCase,
    private val cancelScheduleUseCase: CancelScheduleUseCase,
    private val rescheduleAppUseCase: RescheduleAppUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase
) : ViewModel() {

    private val _schedulesUiState = MutableStateFlow<UiState<List<ScheduleModel>>>(UiState.Loading)
    val schedulesUiState: StateFlow<UiState<List<ScheduleModel>>> = _schedulesUiState.asStateFlow()

    private val _appsUiState = MutableStateFlow<UiState<List<InstalledAppModel>>>(UiState.Idle)
    val appsUiState: StateFlow<UiState<List<InstalledAppModel>>> = _appsUiState.asStateFlow()

    private val _actionUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val actionUiState: StateFlow<UiState<String>> = _actionUiState.asStateFlow()

    fun loadSchedules() {
        viewModelScope.launch {
            getSchedulesUseCase.execute(NoParams)
                .collect { result ->
                    _schedulesUiState.value = when (result) {
                        is Result.Loading ->
                            UiState.Loading

                        is Result.Success ->
                            UiState.Success(result.data)

                        is Result.Error ->
                            UiState.Error(result.message)

                    }
                }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            getInstalledAppsUseCase.execute(NoParams).collect { result ->
                _appsUiState.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> UiState.Success(result.data)
                    is  Result.Error -> UiState.Error(result.message)
                }
            }
        }
    }

    fun scheduleApp(appName: String, packageName: String, scheduledTime: Date) {
        viewModelScope.launch {
            scheduleAppUseCase.execute(
                ScheduleAppUseCase.Params(appName, packageName, scheduledTime)
            ).collect { result ->
                _actionUiState.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> UiState.Success("\"$appName\" scheduled successfully")
                    is Result.Error -> UiState.Error(result.message)
                }
            }
        }
    }

    fun cancelSchedule(schedule: ScheduleModel) {
        viewModelScope.launch {
            cancelScheduleUseCase.execute(schedule).collect { result ->
                _actionUiState.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> UiState.Success("\"${schedule.appName}\" cancelled")
                    is Result.Error -> UiState.Error(result.message)
                }
            }
        }
    }

    fun rescheduleApp(schedule: ScheduleModel, newScheduledTime: Date) {
        viewModelScope.launch {
            rescheduleAppUseCase.execute(
                RescheduleAppUseCase.Params(schedule, newScheduledTime)
            ).collect { result ->
                _actionUiState.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> UiState.Success("\"${schedule.appName}\" rescheduled")
                    is Result.Error -> UiState.Error(result.message)
                }
            }
        }
    }

    fun deleteSchedule(schedule: ScheduleModel) {
        viewModelScope.launch {
            deleteScheduleUseCase.execute(schedule).collect { result ->
                _actionUiState.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> UiState.Success("\"${schedule.appName}\" deleted")
                    is Result.Error -> UiState.Error(result.message)
                }
            }
        }
    }

    fun resetActionState() {
        _actionUiState.value = UiState.Idle
    }

    sealed interface UiState<out T> {
        object Idle : UiState<Nothing>
        data object Loading : UiState<Nothing>
        data class Success<out T>(val data: T) : UiState<T>
        data class Error(val message: String) : UiState<Nothing>
    }
}