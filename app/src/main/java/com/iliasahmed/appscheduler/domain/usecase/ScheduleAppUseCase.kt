package com.iliasahmed.appscheduler.domain.usecase

import com.iliasahmed.appscheduler.data.manager.ScheduleManager
import com.iliasahmed.appscheduler.domain.utils.BaseUseCase
import com.iliasahmed.appscheduler.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class ScheduleAppUseCase @Inject constructor(
    private val scheduleManager: ScheduleManager
) : BaseUseCase<ScheduleAppUseCase.Params, Unit> {

    override suspend fun execute(params: Params): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            when (val result = scheduleManager.scheduleApp(params.appName, params.packageName, params.scheduledTime)) {
                is Result.Success -> emit(Result.Success(Unit))
                is Result.Conflict -> emit(Result.Error("Time conflict with ${result.schedule.appName}, please keep time difference 2 minutes"))
                is Result.Error -> emit(Result.Error(result.message))
                else -> {}
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to schedule app"))
        }
    }

    data class Params(
        val appName: String,
        val packageName: String,
        val scheduledTime: Date
    )
}