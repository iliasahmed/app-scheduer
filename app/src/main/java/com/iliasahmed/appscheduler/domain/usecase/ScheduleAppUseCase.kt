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
            scheduleManager.scheduleApp(
                params.appName,
                params.packageName,
                params.scheduledTime
            )
            emit(Result.Success(Unit))

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