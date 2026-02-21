package com.iliasahmed.appscheduler.ui.scheduleList

import app.cash.turbine.test
import com.iliasahmed.appscheduler.domain.model.InstalledAppModel
import com.iliasahmed.appscheduler.domain.model.ScheduleModel
import com.iliasahmed.appscheduler.domain.model.ScheduleStatus
import com.iliasahmed.appscheduler.domain.usecase.CancelScheduleUseCase
import com.iliasahmed.appscheduler.domain.usecase.DeleteScheduleUseCase
import com.iliasahmed.appscheduler.domain.usecase.GetInstalledAppsUseCase
import com.iliasahmed.appscheduler.domain.usecase.GetSchedulesUseCase
import com.iliasahmed.appscheduler.domain.usecase.RescheduleAppUseCase
import com.iliasahmed.appscheduler.domain.usecase.ScheduleAppUseCase
import com.iliasahmed.appscheduler.domain.utils.NoParams
import com.iliasahmed.appscheduler.domain.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {

    private val getSchedulesUseCase: GetSchedulesUseCase = mockk()
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase = mockk()
    private val scheduleAppUseCase: ScheduleAppUseCase = mockk()
    private val cancelScheduleUseCase: CancelScheduleUseCase = mockk()
    private val rescheduleAppUseCase: RescheduleAppUseCase = mockk()
    private val deleteScheduleUseCase: DeleteScheduleUseCase = mockk()

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ScheduleViewModel

    private fun mockSchedule(name: String) = ScheduleModel(
        id = 1L,
        appName = name,
        packageName = "com.test.app",
        scheduledTime = Date(System.currentTimeMillis() + 60_000),
        status = ScheduleStatus.PENDING,
        createdAt = Date()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = ScheduleViewModel(
            getSchedulesUseCase = getSchedulesUseCase,
            getInstalledAppsUseCase = getInstalledAppsUseCase,
            scheduleAppUseCase = scheduleAppUseCase,
            cancelScheduleUseCase = cancelScheduleUseCase,
            rescheduleAppUseCase = rescheduleAppUseCase,
            deleteScheduleUseCase = deleteScheduleUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial schedulesUiState is Loading`() = runTest {
        viewModel.schedulesUiState.test {
            assertEquals(ScheduleViewModel.UiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial appsUiState is Idle`() = runTest {
        viewModel.appsUiState.test {
            assertEquals(ScheduleViewModel.UiState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial actionUiState is Idle`() = runTest {
        viewModel.actionUiState.test {
            assertEquals(ScheduleViewModel.UiState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSchedules emits Loading then Success`() = runTest {
            val schedules = listOf(mockSchedule("Chrome"))
            coEvery { getSchedulesUseCase.execute(NoParams) } returns flow {
                emit(Result.Loading)
                emit(Result.Success(schedules))
            }

            viewModel.schedulesUiState.test {
                viewModel.loadSchedules()
                advanceUntilIdle()

                assertEquals(ScheduleViewModel.UiState.Loading, awaitItem())

                val successState = awaitItem()
                assert(successState is ScheduleViewModel.UiState.Success<List<ScheduleModel>>)
                assertEquals(schedules, (successState as ScheduleViewModel.UiState.Success<List<ScheduleModel>>).data)
                assertEquals(1, successState.data.size)
                assertEquals("Chrome", successState.data[0].appName)

                cancelAndIgnoreRemainingEvents()
            }
     }

    @Test
    fun `loadSchedules emits Loading then Error`() = runTest {
        val errorMessage = "Database connection failed"
        coEvery { getSchedulesUseCase.execute(NoParams) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(errorMessage))
        }

        viewModel.schedulesUiState.test {
            viewModel.loadSchedules()
            advanceUntilIdle()

            assertEquals(ScheduleViewModel.UiState.Loading, awaitItem())

            val errorState = awaitItem()
            assert(errorState is ScheduleViewModel.UiState.Error)
            assertEquals(errorMessage, (errorState as ScheduleViewModel.UiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSchedules emits Success with empty list`() = runTest {
        coEvery { getSchedulesUseCase.execute(NoParams) } returns flow {
            emit(Result.Success(emptyList()))
        }

        viewModel.schedulesUiState.test {
            assertEquals(ScheduleViewModel.UiState.Loading, awaitItem())

            viewModel.loadSchedules()
            advanceUntilIdle()

            val successState = awaitItem()
            assert(successState is ScheduleViewModel.UiState.Success<List<ScheduleModel>>)
            assertEquals(0, (successState as ScheduleViewModel.UiState.Success<List<ScheduleModel>>).data.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSchedules emits Success with multiple schedules`() = runTest {
        val schedules = listOf(
            mockSchedule("Chrome"),
            mockSchedule("Gmail"),
            mockSchedule("YouTube")
        )

        coEvery { getSchedulesUseCase.execute(NoParams) } returns flow {
            emit(Result.Success(schedules))
        }

        viewModel.schedulesUiState.test {
            assertEquals(ScheduleViewModel.UiState.Loading, awaitItem())

            viewModel.loadSchedules()
            advanceUntilIdle()

            val successState = awaitItem()
            assert(successState is ScheduleViewModel.UiState.Success<List<ScheduleModel>>)
            assertEquals(3, (successState as ScheduleViewModel.UiState.Success<List<ScheduleModel>>).data.size)

            cancelAndIgnoreRemainingEvents()
        }
    }
}