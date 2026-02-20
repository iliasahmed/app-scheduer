package com.iliasahmed.appscheduler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iliasahmed.appscheduler.data.entity.ScheduleEntity

@Database(
    entities = [ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppSchedulerDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}