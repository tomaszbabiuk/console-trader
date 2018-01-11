package org.consoletrader.candles.bitmarket

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BitmarketPublicApi {
    @GET("/json/{pair}/ticker.json")
    fun query(@Query("pair") pair: String): Observable<BitmarketTickerResponse>
}
