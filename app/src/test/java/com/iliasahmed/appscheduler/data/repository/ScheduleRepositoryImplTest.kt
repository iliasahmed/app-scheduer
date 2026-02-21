package com.iliasahmed.appscheduler.data.repository

import android.content.Context
import app.cash.turbine.test
import com.iliasahmed.appscheduler.data.entity.ScheduleEntity
import com.iliasahmed.appscheduler.data.local.ScheduleDao
import com.iliasahmed.appscheduler.domain.utils.Result
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ScheduleRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private var scheduleDao: ScheduleDao = mockk()
    private var context: Context = mockk()
    private lateinit var repository: ScheduleRepositoryImpl

    val mockScheduleEntity = ScheduleEntity(
        id = 1L,
        appName = "YouTube",
        packageName = "com.google.android.youtube",
        scheduledTime = 1700000000000L,
        status = "PENDING",
        createdAt = 1699996400000L,
        executedAt = null,
        workRequestId = "work_001",
        errorMessage = null
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = ScheduleRepositoryImpl(context, scheduleDao)
    }


    @Test
    fun `getAllSchedules emits Success with data`() = runTest(testDispatcher) {
        val entities = listOf(
            mockScheduleEntity,
            mockScheduleEntity.copy(id = 2L)
        )

        coEvery { scheduleDao.getAllSchedules() } returns flowOf(entities)

        repository.getAllSchedules().test {
            val item = awaitItem()

            assertTrue(item is Result.Success)

            val data = (item as Result.Success).data
            assertEquals(2, data.size)
            assertEquals(entities[0].toDomain(), data[0])
            assertEquals(entities[1].toDomain(), data[1])

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `getAllSchedules emits Error`() = runTest {
        val ex = RuntimeException("DB failed")
        coEvery { scheduleDao.getAllSchedules() } returns flow { throw ex }

        repository.getAllSchedules().test {
            val item = awaitItem()
            assertTrue(item is Result.Error)
            assertEquals("DB failed", (item as Result.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}