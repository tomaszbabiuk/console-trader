package org.consoletrader.exchange.kucoin

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface KuCoinPublicApi {
    @GET("/v1/open/chart/history?resolution=30")
    fun queryCandles(@Query("symbol") pair: String, @Query("from") from: Long, @Query("to") to: Long): Single<KuCoinCandleResponse>
}
