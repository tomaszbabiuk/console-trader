package org.consoletrader.exchange.bitfinex

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface BitfinexPublicAPI {
    @GET("/v2/candles/trade:30m:t{pair}/hist")
    fun queryCandles(@Path("pair") pair: String): Observable<List<List<Double>>>
}
