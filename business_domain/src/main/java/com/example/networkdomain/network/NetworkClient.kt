package com.example.networkdomain.network

import com.example.networkdomain.api.EndPoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit.SECONDS

object NetworkClient {

    private const val TIMEOUT_IN_SECONDS = 20L
    private const val CONTENT_TYPE_APPLICATION = "application/json"

    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(EndPoint.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClient)
        .build()


    fun provideOkHttp(
        headerInterceptor: HeaderInterceptor,
        networkManager: NetworkManager
    ): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        httpBuilder.apply {
            addInterceptor(NoInternetInterceptor(networkManager))
            addInterceptor(headerInterceptor)
            connectTimeout(TIMEOUT_IN_SECONDS, SECONDS)
            writeTimeout(TIMEOUT_IN_SECONDS, SECONDS)
            readTimeout(TIMEOUT_IN_SECONDS, SECONDS)
        }
        return httpBuilder.build()
    }
}