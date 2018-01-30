package org.consoletrader.marketcap

import io.reactivex.Single
import org.consoletrader.common.DataSourceTask
import org.consoletrader.marketcap.coinmarketcap.CoinmarketcapMarketcapService

abstract class MarketCapTask : DataSourceTask<MarketCap, MarketCapExtendedParams>() {
    private val marketCapService = CoinmarketcapMarketcapService()

    override fun createParams(paramsRaw: String): MarketCapExtendedParams {
        return MarketCapExtendedParams(paramsRaw);
    }

    override fun createDataSource(params: MarketCapExtendedParams): Single<MarketCap> {
        return marketCapService.create()
    }
}


class MarketCapAboveTask : MarketCapTask() {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapabove")
    }

    override fun verifySuccess(data: MarketCap, params: MarketCapExtendedParams): Boolean {
        return data.value > params.amount
    }
}

class MarketCapBelowTask: MarketCapTask() {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapbelow")
    }

    override fun verifySuccess(data: MarketCap, params: MarketCapExtendedParams): Boolean {
        return data.value < params.amount
    }
}

