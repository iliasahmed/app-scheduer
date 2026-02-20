package com.iliasahmed.appscheduler.di

import android.content.Context
import androidx.room.Room
import com.iliasahmed.appscheduler.data.local.AppSchedulerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideSchedulerDatabase(@ApplicationContext context: Context): AppSchedulerDatabase {
        return Room.databaseBuilder(
            context,
            AppSchedulerDatabase::class.java,
            "app_scheduler_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideSchedulerDao(database: AppSchedulerDatabase) = database.scheduleDao()

}