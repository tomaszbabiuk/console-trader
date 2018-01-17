package org.consoletrader.marketcap.coinmarketcap

import io.reactivex.Single
import retrofit2.http.GET

interface CoinmarketcapPublicAPI {
    @GET("/v1/global/")
    fun query() : Single<CoinmarketcapGlobalResult>
}
