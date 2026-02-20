package com.iliasahmed.appscheduler.data.manager

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.iliasahmed.appscheduler.data.worker.AppLauncherWorker
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.model.ScheduleStatus
import com.iliasahmed.appscheduler.domain.repository.ScheduleRepository
import com.iliasahmed.appscheduler.domain.scheduler.AppScheduler
import com.iliasahmed.appscheduler.domain.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ScheduleManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ScheduleRepository
) : AppScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleApp(
        appName: String,
        packageName: String,
        scheduledTime: Date
    ): Result<Any> {
        return try {
            val now = System.currentTimeMillis()

            if (scheduledTime.time <= now) {
                return Result.Error("Scheduled time must be in the future")
            }

            val schedule = ScheduleModel(
                appName = appName,
                packageName = packageName,
                scheduledTime = scheduledTime,
                status = ScheduleStatus.PENDING,
                createdAt = Date()
            )
            val scheduleId = repository.insertSchedule(schedule)

            val delay = scheduledTime.time - now
            val workRequestId = enqueueWork(packageName, appName, delay, scheduleId)

            val persisted = schedule.copy(
                id = scheduleId,
                workRequestId = workRequestId
            )
            repository.updateSchedule(persisted)

            Result.Success(persisted)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to schedule app")
        }
    }

    override suspend fun cancelSchedule(schedule: ScheduleModel): Boolean {
        return try {
            schedule.workRequestId?.let { id ->
                workManager.cancelWorkById(UUID.fromString(id))
            }
            repository.updateSchedule(
                schedule.copy(
                    status = ScheduleStatus.CANCELLED,
                    executedAt = Date()
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun rescheduleApp(
        schedule: ScheduleModel,
        newScheduledTime: Date
    ): Result<Any> {
        return try {
            val now = System.currentTimeMillis()

            if (newScheduledTime.time <= now) {
                return Result.Error("Scheduled time must be in the future")
            }

            schedule.workRequestId?.let { id ->
                workManager.cancelWorkById(UUID.fromString(id))
            }

            val delay = newScheduledTime.time - now
            val newWorkRequestId =
                enqueueWork(schedule.packageName, schedule.appName, delay, schedule.id)

            val updated = schedule.copy(
                scheduledTime = newScheduledTime,
                workRequestId = newWorkRequestId,
                status = ScheduleStatus.PENDING
            )
            repository.updateSchedule(updated)

            Result.Success(updated)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to reschedule app")
        }
    }

    private fun enqueueWork(
        packageName: String,
        appName: String,
        delayMillis: Long,
        scheduleId: Long
    ): String {
        val inputData = Data.Builder()
            .putString(AppLauncherWorker.PACKAGE_NAME_KEY, packageName)
            .putString(AppLauncherWorker.APP_NAME_KEY, appName)
            .putLong(AppLauncherWorker.SCHEDULE_ID_KEY, scheduleId)
            .build()

        val request = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueue(request)
        return request.id.toString()
    }
}