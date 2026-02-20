package com.iliasahmed.appscheduler.domain.model

data class InstalledAppModel(
    val appName: String,
    val packageName: String,
    val versionName: String?,
    val isSystemApp: Boolean
)