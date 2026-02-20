package com.iliasahmed.appscheduler.domain.usecase

import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.repository.ScheduleRepository
import com.iliasahmed.appscheduler.domain.utils.BaseUseCase
import com.iliasahmed.appscheduler.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository,
    private val cancelScheduleUseCase: CancelScheduleUseCase
): BaseUseCase<ScheduleModel, Unit>{
    override suspend fun execute(params: ScheduleModel): Flow<Result<Unit>> = flow {
        try {
            if (params.isCancellable()) {
                cancelScheduleUseCase.execute(params).collect { result ->
                    result.let {
                        if (it is Result.Error) {
                            emit(Result.Error(it.message))
                        } else {
                            emit(Result.Success(Unit))
                        }
                    }
                }
            }

            repository.deleteSchedule(params)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Failed to delete schedule"))
        }
    }
}