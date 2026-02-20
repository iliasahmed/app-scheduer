package com.iliasahmed.appscheduler.domain.usecase

import com.iliasahmed.appscheduler.data.manager.ScheduleManager
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.utils.BaseUseCase
import com.iliasahmed.appscheduler.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CancelScheduleUseCase @Inject constructor(
    private val manager: ScheduleManager
): BaseUseCase<ScheduleModel, ScheduleModel> {
    override suspend fun execute(params: ScheduleModel): Flow<Result<ScheduleModel>> = flow {
        try {
            manager.cancelSchedule(schedule = params)
            emit(Result.Success(params))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to cancel schedule"))
        }
    }
}