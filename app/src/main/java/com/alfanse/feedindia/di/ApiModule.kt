package com.alfanse.feedindia.di


import android.app.Application
import android.content.Context
import com.alfanse.feedindia.data.ApiHeadersInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.alfanse.feedindia.data.ApiService
import com.alfanse.feedindia.data.HeaderEntity
import com.alfanse.feedindia.utils.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
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
    internal fun provideOkhttpClient(cache: Cache, utils: Utils): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor {
            val headerEntity = HeaderEntity(
                "be7a16fae793838c7ef167714ba36d2e",
                utils.getAppVersionName(),
                utils.getAppVersionCode().toString(),
                utils.getRandomString(),
                utils.getDeviceId())
            val apiHeaders = ApiHeadersInterceptor(it)
            apiHeaders.buildHeader(headerEntity)
        }
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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Provides
    @Singleton
    internal fun provideGithubApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
