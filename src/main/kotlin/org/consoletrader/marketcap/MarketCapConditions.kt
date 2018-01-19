package org.consoletrader.marketcap

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.EvaluationResult
import org.consoletrader.marketcap.coinmarketcap.CoinmarketcapMarketcapService

abstract class MarketCapCondition : Condition {
    private val marketCapService = CoinmarketcapMarketcapService()

    override fun buildEvaluator(): Single<EvaluationResult> {
        return marketCapService
                .createSingle()
                .map(this::mapper)
                .onErrorResumeNext { throwable -> Single.just(EvaluationResult(false, "Exception: $throwable")) }
    }

    abstract fun mapper(marketCap:MarketCap): EvaluationResult
}


class MarketCapAboveCondition(private val params: MarketCapExtendedParams) : MarketCapCondition() {
    override fun mapper(marketCap: MarketCap): EvaluationResult {
        val result = marketCap.value > params.amount
        val comment = if (result) {
            "[TRUE] Marketcap: $marketCap > ${MarketCap(params.amount)}"
        } else {
            "[FALSE] Marketcap: $marketCap < ${MarketCap(params.amount)}"
        }
        return EvaluationResult(result, comment)
    }
}

class MarketCapBelowCondition(private val params: MarketCapExtendedParams) : MarketCapCondition() {
    override fun mapper(marketCap: MarketCap): EvaluationResult {
        val result = marketCap.value < params.amount
        val comment = if (result) {
            "[TRUE] Marketcap: $marketCap < ${MarketCap(params.amount)}"
        } else {
            "[FALSE] Marketcap: $marketCap > ${MarketCap(params.amount)}"
        }
        return EvaluationResult(result, comment)
    }
}