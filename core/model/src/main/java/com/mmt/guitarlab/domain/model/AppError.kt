package com.mmt.guitarlab.domain.model

sealed interface AppError {
    data class NetworkError(val message: String? = null) : AppError
    data class DatabaseError(val message: String? = null) : AppError
    data class ParseError(val message: String? = null) : AppError
    data class AudioError(val message: String? = null) : AppError
    data class UnknownError(val throwable: Throwable? = null) : AppError
}

inline fun <T> runCatchingApp(block: () -> T): Result<T> {
    return runCatching {
        block()
    }.onFailure { throwable ->
        // Here we can map exceptions to AppError if needed or wrap them
    }
}
