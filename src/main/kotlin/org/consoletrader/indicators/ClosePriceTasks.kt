package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.DataSourceTask
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

abstract class ClosePriceTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairAndDoubleExtendedParams>() {
    override fun createDataSource(params: PairAndDoubleExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }

    override fun createParams(paramsRaw: String): PairAndDoubleExtendedParams {
        return PairAndDoubleExtendedParams(paramsRaw)
    }
}

class ClosePriceAboveTask(exchangeManager: ExchangeManager) : ClosePriceTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val closePrice = closePriceIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = closePrice > params.value
        println("[${passed.toString().toUpperCase()}] Close price of ${params.currencyPair}: $closePrice > ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("closepriceabove")
    }
}

class ClosePriceBelowTask(exchangeManager: ExchangeManager) : ClosePriceTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val closePrice = closePriceIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = closePrice < params.value
        println("[${passed.toString().toUpperCase()}] Close price of ${params.currencyPair}: $closePrice < ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("closepricebelow")
    }
}