package com.example.gf5.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("Retrofit", "Sending request: ${request.url} on ${chain.connection()}")
        Log.d("Retrofit", "Request headers: ${request.headers}")

        val response = chain.proceed(request)

        Log.d("Retrofit", "Received response for ${response.request.url} with status ${response.code}")
        Log.d("Retrofit", "Response headers: ${response.headers}")

        return response
    }
}
