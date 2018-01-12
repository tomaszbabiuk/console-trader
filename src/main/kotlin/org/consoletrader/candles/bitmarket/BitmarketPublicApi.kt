package org.consoletrader.candles.bitmarket

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BitmarketPublicApi {
    @GET("/json/{pair}/ticker.json")
    fun queryCandles(@Query("pair") pair: String): Single<List<BitmarketTickerResponse>>
}
