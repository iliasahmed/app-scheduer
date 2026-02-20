package com.iliasahmed.appscheduler.di

import com.iliasahmed.appscheduler.data.manager.ScheduleManager
import com.iliasahmed.appscheduler.domain.scheduler.AppScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerModule {

    @Binds
    @Singleton
    abstract fun bindAppScheduler(
        scheduleManager: ScheduleManager
    ): AppScheduler
}