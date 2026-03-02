package com.iliasahmed.appscheduler.domain.utils

import com.iliasahmed.appscheduler.domain.model.ScheduleModel

sealed interface Result<out R> {
    data object Loading : Result<Nothing>
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val message: String) : Result<Nothing>
    data class Conflict(val schedule: ScheduleModel) : Result<Unit>

}