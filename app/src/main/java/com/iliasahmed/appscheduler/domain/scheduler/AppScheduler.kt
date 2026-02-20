package com.iliasahmed.appscheduler.domain.scheduler

import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.utils.Result
import java.util.Date

interface AppScheduler {

    suspend fun scheduleApp(
        appName: String,
        packageName: String,
        scheduledTime: Date
    ): Result<Any>

    suspend fun cancelSchedule(schedule: ScheduleModel): Boolean

    suspend fun rescheduleApp(
        schedule: ScheduleModel,
        newScheduledTime: Date
    ): Result<Any>
}