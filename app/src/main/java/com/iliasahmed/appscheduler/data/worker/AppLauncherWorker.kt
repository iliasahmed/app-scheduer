package com.iliasahmed.appscheduler.data.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iliasahmed.appscheduler.domain.model.ScheduleStatus
import com.iliasahmed.appscheduler.domain.repository.ScheduleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.Date

@HiltWorker
class AppLauncherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ScheduleRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val packageName = inputData.getString(PACKAGE_NAME_KEY) ?: run {
            return Result.failure()
        }

        val scheduleId = inputData.getLong(SCHEDULE_ID_KEY, -1L)

        if (scheduleId == -1L) {
            return Result.failure()
        }

        return try {
            val schedule = repository.getScheduleById(scheduleId) ?: return Result.failure()

            val launchIntent = applicationContext.packageManager
                .getLaunchIntentForPackage(packageName)
                ?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                }

            if (launchIntent != null) {
                applicationContext.startActivity(launchIntent)

                repository.updateSchedule(
                    schedule.copy(
                        status = ScheduleStatus.COMPLETED,
                        executedAt = Date()
                    )
                )

                Result.success()
            } else {
                repository.updateSchedule(
                    schedule.copy(
                        status = ScheduleStatus.FAILED,
                        executedAt = Date(),
                    )
                )

                Result.failure()
            }
        } catch (e: Exception) {
            try {
                val schedule = repository.getScheduleById(scheduleId)
                schedule?.let {
                    repository.updateSchedule(
                        it.copy(
                            status = ScheduleStatus.FAILED,
                            executedAt = Date(),
                        )
                    )
                }
            } catch (updateError: Exception) {
                Timber.e(updateError, "Failed to update schedule status")
            }

            Result.failure()
        }
    }

    companion object {
        const val PACKAGE_NAME_KEY = "PACKAGE_NAME_KEY"
        const val APP_NAME_KEY = "APP_NAME_KEY"
        const val SCHEDULE_ID_KEY = "SCHEDULE_ID_KEY"
    }
}