package com.iliasahmed.appscheduler.domain.usecase

import com.iliasahmed.appscheduler.data.manager.ScheduleManager
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.utils.BaseUseCase
import com.iliasahmed.appscheduler.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

class RescheduleAppUseCase @Inject constructor(
    private val manager: ScheduleManager
): BaseUseCase<RescheduleAppUseCase.Params, Unit> {

    override suspend fun execute(params: Params): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)
            manager.rescheduleApp(params.schedule, params.newScheduledTime)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to reschedule app"))
        }
    }
    data class Params(
        val schedule: ScheduleModel,
        val newScheduledTime: Date
    )
}