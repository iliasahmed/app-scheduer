package com.iliasahmed.appscheduler.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.iliasahmed.appscheduler.R
import com.iliasahmed.appscheduler.data.entity.ScheduleEntity
import com.iliasahmed.appscheduler.data.local.ScheduleDao
import com.iliasahmed.appscheduler.domain.model.InstalledAppModel
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.repository.ScheduleRepository
import com.iliasahmed.appscheduler.domain.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override suspend fun getAllSchedules(): Flow<Result<List<ScheduleModel>>> = scheduleDao.getAllSchedules()
        .map<List<ScheduleEntity>, Result<List<ScheduleModel>>> { entities -> Result.Success(entities.map { it.toDomain() }) }
        .catch { e -> emit(Result.Error(e.message ?: context.getString(R.string.msg_error_occurred))) }
        .flowOn(Dispatchers.IO)


    override suspend fun getScheduleById(id: Long): ScheduleModel? {
        return withContext(Dispatchers.IO) {
            scheduleDao.getScheduleById(id)?.toDomain()
        }
    }

    override suspend fun insertSchedule(schedule: ScheduleModel): Long {
        return withContext(Dispatchers.IO) {
            val entity = ScheduleEntity.fromDomain(schedule)
            scheduleDao.insertSchedule(entity)
        }
    }

    override suspend fun updateSchedule(schedule: ScheduleModel) {
        withContext(Dispatchers.IO) {
            val entity = ScheduleEntity.fromDomain(schedule)
            scheduleDao.updateSchedule(entity)
        }
    }

    override suspend fun deleteSchedule(schedule: ScheduleModel) {
        withContext(Dispatchers.IO) {
            val entity = ScheduleEntity.fromDomain(schedule)
            scheduleDao.deleteSchedule(entity)
        }
    }

    override suspend fun getInstalledApps(): Flow<Result<List<InstalledAppModel>>> = flow {
        try {
            val packageManager = context.packageManager

            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            val installedApps = packages
                .filter { isLaunchableApp(it, packageManager) }
                .map { appInfo ->
                    InstalledAppModel(
                        appName = appInfo.loadLabel(packageManager).toString(),
                        packageName = appInfo.packageName,
                        versionName = try {
                            packageManager.getPackageInfo(appInfo.packageName, 0).versionName
                        } catch (e: Exception) {
                            null
                        },
                        isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    )
                }
                .sortedBy { it.appName }
            emit(Result.Success(installedApps))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: context.getString(R.string.msg_error_occurred)))
        }
    }.flowOn(Dispatchers.IO)

    private fun isLaunchableApp(
        appInfo: ApplicationInfo,
        packageManager: PackageManager
    ): Boolean {
        return packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
    }

}