package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

abstract class RsiTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairAndDoubleExtendedParams>() {
    override fun createParams(paramsRaw: String): PairAndDoubleExtendedParams {
        return PairAndDoubleExtendedParams(paramsRaw)
    }

    override fun createDataSource(params: PairAndDoubleExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }
}

class RsiAboveTask(exchangeManager: ExchangeManager) : RsiTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val rsiIndicator = RSIIndicator(closePriceIndicator, 14)
        val rsi = rsiIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = rsi > params.value
        println("[${passed.toString().toUpperCase()}] RSI of ${params.currencyPair}: $rsi > ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("rsiabove")
    }
}

class RsiBelowTask(exchangeManager: ExchangeManager) : RsiTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val rsiIndicator = RSIIndicator(closePriceIndicator, 14)
        val rsi = rsiIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = rsi < params.value
        println("[${passed.toString().toUpperCase()}] RSI of ${params.currencyPair}: $rsi < ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("rsibelow")
    }
}
