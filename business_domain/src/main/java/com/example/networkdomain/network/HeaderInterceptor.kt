package com.example.networkdomain.network

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Chain): Response {

        return "prefsUtil".let {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
            )
        }
    }
}