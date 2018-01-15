package org.consoletrader.candles.base

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseApi<out T>(anApi: Class<T>,
                              endpoint: String,
                              timeout: Long = 30,
                              timeUnit: TimeUnit = TimeUnit.SECONDS) {

    private val api: T

    init {
        val client = OkHttpClient.Builder()
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