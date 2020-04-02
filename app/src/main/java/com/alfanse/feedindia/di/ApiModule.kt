package com.alfanse.feedindia.di


import android.app.Application
import com.alfanse.feedindia.data.ApiService
import com.alfanse.feedindia.data.interceptor.HeadersInterceptor
import com.alfanse.feedindia.data.storage.ApplicationStorage
import com.alfanse.feedindia.factory.CustomConverterFactory
import com.alfanse.feedindia.utils.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class ApiModule {

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideCache(application: Application): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val httpCacheDirectory = File(application.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }


    @Provides
    @Singleton
    internal fun provideOkhttpClient(cache: Cache, utils: Utils, @Named("memory") memoryApplicationStorage: ApplicationStorage): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        val headers = HeadersInterceptor(utils, memoryApplicationStorage)
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor(headers)
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        return httpClient.build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(CustomConverterFactory(gson))
            .build()
    }


    @Provides
    @Singleton
    internal fun provideGithubApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
