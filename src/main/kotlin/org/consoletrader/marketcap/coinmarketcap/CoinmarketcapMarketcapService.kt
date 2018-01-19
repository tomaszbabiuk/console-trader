package org.consoletrader.marketcap.coinmarketcap

import io.reactivex.Observable
import io.reactivex.Single
import org.consoletrader.common.BaseApi
import org.consoletrader.common.DataSource
import org.consoletrader.marketcap.MarketCap

class CoinmarketcapMarketcapService : BaseApi<CoinmarketcapPublicAPI>(
        anApi = CoinmarketcapPublicAPI::class.java,
        endpoint = "https://api.coinmarketcap.com"),
        DataSource<MarketCap> {
    override fun createSingle(): Single<MarketCap> {
        return getApi()
                .query()
                .map {
                    MarketCap(it.totalMarketCapUsd)
                }
    }

    override fun createObservable(): Observable<MarketCap> {
        return createSingle()
                .toObservable()
    }
}


