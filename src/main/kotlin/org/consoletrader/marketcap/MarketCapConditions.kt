package org.consoletrader.marketcap

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.WatcherFactory
import org.consoletrader.marketcap.coinmarketcap.CoinmarketcapMarketcapService

abstract class MarketCapWatcherFactory : WatcherFactory {
    override fun create(paramsRaw: String): Condition {
        val marketCapExtendedParams = MarketCapExtendedParams(paramsRaw)
        return create(marketCapExtendedParams)
    }

    abstract fun create(marketCap: MarketCapExtendedParams): Condition
}

class MarketCapAboveWatcherFactory : MarketCapWatcherFactory() {
    override fun create(marketCap: MarketCapExtendedParams): Condition {
        return MarketCapAboveCondition(marketCap)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapabove")
    }
}

class MarketCapBelowWatcherFactory : MarketCapWatcherFactory() {
    override fun create(marketCap: MarketCapExtendedParams): Condition {
        return MarketCapBelowCondition(marketCap)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapbelow")
    }
}

abstract class MarketCapCondition(private val params: MarketCapExtendedParams) : Condition {
    private val marketCapService = CoinmarketcapMarketcapService()

    override fun buildEvaluator(): Single<Boolean> {
        return marketCapService
                .createSingle()
                .map(this::mapper)
                .onErrorResumeNext { _ -> Single.just(false) }
    }

    abstract fun mapper(marketCap:MarketCap): Boolean
}


class MarketCapAboveCondition(private val params: MarketCapExtendedParams) : MarketCapCondition(params) {
    override fun mapper(marketCap: MarketCap): Boolean {
        return marketCap.value > params.amount//To change body of created functions use File | Settings | File Templates.
    }
}

class MarketCapBelowCondition(private val params: MarketCapExtendedParams) : MarketCapCondition(params) {
    override fun mapper(marketCap: MarketCap): Boolean {
        return marketCap.value < params.amount//To change body of created functions use File | Settings | File Templates.
    }
}