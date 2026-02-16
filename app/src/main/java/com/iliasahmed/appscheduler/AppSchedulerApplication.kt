package com.iliasahmed.appscheduler

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppSchedulerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}