package com.iliasahmed.appscheduler.domain.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

enum class ScheduleStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    FAILED
}

data class ScheduleModel(
    val id: Long = 0,
    val appName: String,
    val packageName: String,
    val scheduledTime: Date,
    val status: ScheduleStatus,
    val createdAt: Date,
    val executedAt: Date? = null,
    val workRequestId: String? = null,
): Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        appName = parcel.readString().orEmpty(),
        packageName = parcel.readString().orEmpty(),
        scheduledTime = Date(parcel.readLong()),
        status = ScheduleStatus.valueOf(parcel.readString()!!),
        createdAt = Date(parcel.readLong()),
        executedAt = parcel.readLong().let { if (it == -1L) null else Date(it) },
        workRequestId = parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(appName)
        parcel.writeString(packageName)
        parcel.writeLong(scheduledTime.time)
        parcel.writeString(status.name)
        parcel.writeLong(createdAt.time)
        parcel.writeLong(executedAt?.time ?: -1L)
        parcel.writeString(workRequestId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ScheduleModel> {
        override fun createFromParcel(parcel: Parcel): ScheduleModel = ScheduleModel(parcel)
        override fun newArray(size: Int): Array<ScheduleModel?> = arrayOfNulls(size)
    }

    fun isCancellable(): Boolean {
        return status == ScheduleStatus.PENDING &&
                scheduledTime.time > System.currentTimeMillis()
    }

    fun isEditable(): Boolean {
        return status == ScheduleStatus.PENDING
    }
}