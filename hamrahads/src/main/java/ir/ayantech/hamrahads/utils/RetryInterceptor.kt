package ir.ayantech.hamrahads.utils

import ir.ayantech.hamrahads.network.model.ErrorType
import ir.ayantech.hamrahads.network.model.NetworkError
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RetryInterceptor(
    private val maxRetries: Int = 3
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response
        var retryCount = 0
        var lastException: Exception? = null

        while (retryCount < maxRetries) {
            try {
                response = chain.proceed(request)
                if (response.isSuccessful || !shouldRetry(response.code)) {
                    return response
                }
                response.close()
            } catch (e: Exception) {
                lastException = e
                if (!shouldRetry(e)) {
                    throw e
                }
            }
            retryCount++
        }
        throw lastException ?: IOException(NetworkError().getError(4, ErrorType.Remote).description)
    }

    private fun shouldRetry(errorCode: Int): Boolean {
        return errorCode == 408 || errorCode >= 500
    }

    private fun shouldRetry(exception: Exception): Boolean {
        return when (exception) {
            is SSLHandshakeException -> false
            is UnknownHostException -> false
            else -> exception is SocketTimeoutException || exception is IOException
        }
    }
}