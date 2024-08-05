package umc.cozymate.util

import retrofit2.Response
import umc.cozymate.CozyMateApplication
import umc.cozymate.module.NetworkModule

suspend fun <T : Any, R : Any> handleApi(
    execute: suspend () -> Response<T>,
    mapper: (T) -> R
): NetworkResult<R> {
    if (CozyMateApplication.isOnline().not()) {
        return NetworkResult.Error(Exception(NetworkModule.NETWORK_EXCEPTION_OFFLINE_CASE))
    }

    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful) {
            body?.let {
                NetworkResult.Success(mapper(it))
            } ?: run {
                throw NullPointerException(NetworkModule.NETWORK_EXCEPTION_BODY_IS_NULL)
            }
        } else {
            getFailDataResult(body, response)
        }
    } catch (e: Exception) {
        NetworkResult.Error(e)
    }
}


private fun <T : Any> getFailDataResult(body: T?, response: Response<T>) = body?.let {
    NetworkResult.Fail(statusCode = response.code(), message = it.toString())
} ?: run {
    NetworkResult.Fail(statusCode = response.code(), message = response.message())
}