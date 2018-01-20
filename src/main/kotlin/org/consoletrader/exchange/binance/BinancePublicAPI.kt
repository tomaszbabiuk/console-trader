package org.consoletrader.exchange.binance

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BinancePublicAPI {
    @GET("/api/v1/klines?interval=30m")
    fun queryCandles(@Query("symbol") symbol: String): Observable<List<List<String>>>
}
