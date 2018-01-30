package org.consoletrader.indicators

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams
import org.consoletrader.common.Task
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import kotlin.system.exitProcess

abstract class ClosePriceTask(val exchangeManager: ExchangeManager) : Task {
    override fun execute(paramsRaw: String) {
        val params = PairAndDoubleExtendedParams(paramsRaw)
        val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)
        dataSource
                .create()
                .map { it.series }
                .doOnSuccess {
                    val result = checkClosePrice(it, params)
                    if (result) {
                        exitProcess(0)
                    } else {
                        exitProcess(1)
                    }
                }
                .doOnError {
                    println(it)
                    exitProcess(1)
                }
                .blockingGet()
    }

    abstract fun checkClosePrice(series: TimeSeries, params: PairAndDoubleExtendedParams): Boolean
}

class ClosePriceAboveTask(exchangeManager: ExchangeManager) : ClosePriceTask(exchangeManager) {
    override fun checkClosePrice(series: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(series)
        val closePrice = closePriceIndicator.getValue(series.tickCount - 1).toDouble()
        val passed = closePrice > params.value
        println("[${passed.toString().toUpperCase()}] Close price of ${params.currencyPair}: $closePrice > ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("closepriceabove")
    }
}

class ClosePriceBelowTask(exchangeManager: ExchangeManager) : ClosePriceTask(exchangeManager) {
    override fun checkClosePrice(series: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(series)
        val closePrice = closePriceIndicator.getValue(series.tickCount - 1).toDouble()
        val passed = closePrice < params.value
        println("[${passed.toString().toUpperCase()}] Close price of ${params.currencyPair}: $closePrice < ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("closepricebelow")
    }
}