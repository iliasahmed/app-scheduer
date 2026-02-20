package com.iliasahmed.appscheduler.domain.utils

import kotlinx.coroutines.flow.Flow

interface UseCase

interface BaseUseCase<Params, Type>: UseCase {
    suspend fun execute(params: Params): Flow<Result<Type>>
}

object NoParams