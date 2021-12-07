package com.example.networkdomain.network

import android.annotation.SuppressLint
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException

@SuppressLint("MissingPermission")
class NoInternetInterceptor(private val networkManager: NetworkManager) : Interceptor {
    override fun intercept(chain: Chain): Response {
        return if (!networkManager.isInternetAvailable()) {
            throw NoInternetException()
        } else {
            chain.proceed(chain.request())
        }
    }
}

class NoInternetException : IOException() {

}
