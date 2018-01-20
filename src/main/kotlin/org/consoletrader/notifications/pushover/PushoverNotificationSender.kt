package org.consoletrader.notifications.pushover

import okhttp3.OkHttpClient
import org.consoletrader.notifications.NotificationsSender
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PushoverNotificationSender : NotificationsSender {

    private var api: PushoverApi

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val builder = Retrofit.Builder()
                .baseUrl("https://api.pushover.net")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        api = builder.create(PushoverApi::class.java)
    }

    override fun sendMessage(apiKey: String, userId: String, message: String) {
        val params = PushoverRequestParams(apiKey, userId, message)
        api.sendPush(params)
                .subscribe(this::handleSuccess, this::handleError)
    }

    private fun handleSuccess(response: Void?) {
        println("push ok")
    }

    private fun handleError(error: Throwable) {
        println("push failed: $error")
    }
}