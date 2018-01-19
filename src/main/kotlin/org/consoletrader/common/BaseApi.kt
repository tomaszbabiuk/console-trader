package org.consoletrader.common

import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

abstract class BaseApi<out T>(anApi: Class<T>,
                              endpoint: String,
                              timeout: Long = 5,
                              timeUnit: TimeUnit = TimeUnit.SECONDS) {

    private val api: T

    init {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cacheDir = File("./cache")
        val cache = Cache(cacheDir, cacheSize.toLong())

        val client = OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(timeout, timeUnit)
                .readTimeout(timeout, timeUnit)
                .build()

        val builder = Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        api = builder.create(anApi)
    }

    fun getApi() = api
}