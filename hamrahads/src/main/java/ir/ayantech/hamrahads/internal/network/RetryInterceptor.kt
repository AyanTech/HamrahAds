package ir.ayantech.hamrahads.internal.network

import ir.ayantech.hamrahads.model.error.ErrorType
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RetryInterceptor(
    private val maxRetries: Int = 3
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!isRetryableRequest(request)) {
            return chain.proceed(request)
        }

        var retryCount = 0
        var lastException: IOException? = null

        while (retryCount < maxRetries) {
            try {
                return chain.proceed(request)
            } catch (e: IOException) {
                lastException = e
                if (!shouldRetry(e)) {
                    throw e
                }

                retryCount++
                if (retryCount >= maxRetries) {
                    break
                }

                val delayMillis = backoffDelayMillis(retryCount)
                if (delayMillis > 0) {
                    try {
                        Thread.sleep(delayMillis)
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw e
                    }
                }
            }
        }
        throw lastException ?: IOException(HamrahAdsError().getError(4, ErrorType.Remote).description)
    }

    private fun isRetryableRequest(request: okhttp3.Request): Boolean {
        if (maxRetries <= 1) return false
        if (request.method != "GET") return false

        val path = request.url.encodedPath
        return path.contains("/ads/") || path.contains("/init/")
    }

    private fun shouldRetry(exception: IOException): Boolean {
        return when (exception) {
            is SSLHandshakeException -> false
            is UnknownHostException -> false
            else -> true
        }
    }

    private fun backoffDelayMillis(retryCount: Int): Long {
        val base = 200L
        val multiplier = 1L shl (retryCount - 1).coerceAtMost(4)
        return (base * multiplier).coerceAtMost(1_000L)
    }
}
