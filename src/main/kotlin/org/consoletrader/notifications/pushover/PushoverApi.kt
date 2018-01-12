package org.consoletrader.notifications.pushover

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface PushoverApi {

    @POST("/1/messages.json")
    fun sendPush(@Body params: PushoverRequestParams): Observable<Void>
}