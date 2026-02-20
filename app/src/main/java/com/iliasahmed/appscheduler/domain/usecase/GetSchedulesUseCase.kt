package com.iliasahmed.appscheduler.domain.usecase

import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.repository.ScheduleRepository
import com.iliasahmed.appscheduler.domain.utils.BaseUseCase
import com.iliasahmed.appscheduler.domain.utils.NoParams
import com.iliasahmed.appscheduler.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSchedulesUseCase @Inject constructor(
    private val repository: ScheduleRepository
) : BaseUseCase<NoParams, List<ScheduleModel>> {
    override suspend fun execute(params: NoParams): Flow<Result<List<ScheduleModel>>> =
        repository.getAllSchedules()
}