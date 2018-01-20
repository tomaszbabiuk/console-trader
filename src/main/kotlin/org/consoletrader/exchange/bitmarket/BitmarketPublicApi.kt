package org.consoletrader.exchange.bitmarket

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BitmarketPublicApi {
    @GET("/graphs/{pair}/1d.json")
    fun queryCandles(@Query("pair") pair: String): Single<List<BitmarketTickerResponse>>
}
