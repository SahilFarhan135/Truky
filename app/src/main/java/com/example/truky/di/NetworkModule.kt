package com.example.truky.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.networkdomain.api.TrucksApi
import com.example.networkdomain.network.HeaderInterceptor
import com.example.networkdomain.network.NetworkClient
import com.example.networkdomain.network.NetworkManager
import com.example.networkdomain.repository.truckrepository.TruckRepository
import com.example.networkdomain.repository.truckrepository.TruckRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): TrucksApi {
        return retrofit.create(TrucksApi::class.java)
    }


    @Provides
    @Singleton
    fun provideCoinRepository(api: TrucksApi): TruckRepository {
        return TruckRepositoryImpl(api)
    }

    @Singleton
    @Provides
    fun provideNetworkManager(
        @ApplicationContext context: Context
    ): NetworkManager {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return NetworkManager(connectivityManager)
    }

/*
    @Singleton
    @Provides
    fun provideHeaderInterceptor(
        prefsUtil: PrefsUtil
    ): HeaderInterceptor = HeaderInterceptor(prefsUtil)
*/

    @Singleton
    @Provides
    fun provideOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        networkManager: NetworkManager
    ): OkHttpClient {
        return NetworkClient.provideOkHttp(
            headerInterceptor,
            networkManager
        )
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = NetworkClient.provideRetrofit(okHttpClient)


}
