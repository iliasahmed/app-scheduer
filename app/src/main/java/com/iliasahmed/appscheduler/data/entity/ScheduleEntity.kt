package com.iliasahmed.appscheduler.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.model.ScheduleStatus
import java.util.Date

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String,
    val packageName: String,
    val scheduledTime: Long,
    val status: String,
    val createdAt: Long,
    val executedAt: Long? = null,
    val workRequestId: String? = null,
    val errorMessage: String? = null

) {

    fun toDomain(): ScheduleModel {
        return ScheduleModel(
            id = id,
            appName = appName,
            packageName = packageName,
            scheduledTime = Date(scheduledTime),
            status = ScheduleStatus.valueOf(status),
            createdAt = Date(createdAt),
            executedAt = executedAt?.let { Date(it) },
            workRequestId = workRequestId,
        )
    }

    companion object {
        fun fromDomain(schedule: ScheduleModel): ScheduleEntity {
            return ScheduleEntity(
                id = schedule.id,
                appName = schedule.appName,
                packageName = schedule.packageName,
                scheduledTime = schedule.scheduledTime.time,
                status = schedule.status.name,
                createdAt = schedule.createdAt.time,
                executedAt = schedule.executedAt?.time,
                workRequestId = schedule.workRequestId,
            )
        }
    }
}