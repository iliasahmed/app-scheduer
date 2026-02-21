package com.iliasahmed.appscheduler.domain.usecase

import app.cash.turbine.test
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.model.ScheduleStatus
import com.iliasahmed.appscheduler.domain.repository.ScheduleRepository
import com.iliasahmed.appscheduler.domain.utils.NoParams
import com.iliasahmed.appscheduler.domain.utils.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

@ExperimentalCoroutinesApi
class GetSchedulesUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    private var repository: ScheduleRepository = mockk()
    private lateinit var useCase: GetSchedulesUseCase

    private fun mockSchedule(name: String) = ScheduleModel(
        id = 1L,
        appName = name,
        packageName = "com.test.app",
        scheduledTime = Date(System.currentTimeMillis() + 60_000),
        status = ScheduleStatus.PENDING,
        createdAt = Date()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetSchedulesUseCase(repository)
    }

    @Test
    fun `execute emits Loading then Success`() = runTest {
        val schedules = listOf(
            mockSchedule("Chrome"),
            mockSchedule("Gmail"),
            mockSchedule("YouTube")
        )

        coEvery { repository.getAllSchedules() } returns flow {
            emit(Result.Loading)
            emit(Result.Success(schedules))
        }

        val resultFlow = useCase.execute(NoParams)

        resultFlow.test {
            assertEquals(Result.Loading, awaitItem())
            assertEquals(Result.Success(schedules), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `execute emits Error`() = runTest {
        val errorMessage = "error"
        coEvery { repository.getAllSchedules() } returns flow {
            emit(Result.Loading)
            emit(Result.Error(errorMessage))
        }

        val resultFlow = useCase.execute(NoParams)

        resultFlow.test {
            assertEquals(Result.Loading, awaitItem())
            val error = awaitItem()
            assert(error is Result.Error)
            assertEquals(errorMessage, (error as Result.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}