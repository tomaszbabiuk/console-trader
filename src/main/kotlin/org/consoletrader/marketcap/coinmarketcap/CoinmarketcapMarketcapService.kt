package org.consoletrader.marketcap.coinmarketcap

import io.reactivex.Observable
import org.consoletrader.candles.base.BaseApi
import org.consoletrader.common.DataSource
import org.consoletrader.marketcap.Marketcap

class CoinmarketcapMarketcapService : BaseApi<CoinmarketcapPublicAPI>(
        anApi = CoinmarketcapPublicAPI::class.java,
        endpoint = "https://api.coinmarketcap.com"),
        DataSource<Marketcap> {
    override fun createObservable(): Observable<Marketcap> {
        return getApi()
                .query()
                .map {
                    Marketcap(it.totalMarketCapUsd)
                }
                .toObservable()
    }
}


