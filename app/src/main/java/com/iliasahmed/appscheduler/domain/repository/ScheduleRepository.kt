package com.iliasahmed.appscheduler.domain.repository

import com.iliasahmed.appscheduler.domain.model.InstalledAppModel
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun getAllSchedules(): Flow<Result<List<ScheduleModel>>>
    suspend fun getScheduleById(id: Long): ScheduleModel?
    suspend fun insertSchedule(schedule: ScheduleModel): Long
    suspend fun updateSchedule(schedule: ScheduleModel)
    suspend fun deleteSchedule(schedule: ScheduleModel)
    suspend fun getInstalledApps(): Flow<Result<List<InstalledAppModel>>>
}