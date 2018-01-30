package org.consoletrader.indicators

import org.consoletrader.common.*
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import kotlin.system.exitProcess

abstract class MacdTask(val exchangeManager: ExchangeManager) : Task {

    override fun execute(paramsRaw: String) {
        val params = PairExtendedParams(paramsRaw)
        val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)
        dataSource
                .create()
                .map { it.series }
                .doOnSuccess {
                    println(it)

                    val result = verify(it, params)
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

    abstract fun verify(series: TimeSeries, params: PairExtendedParams): Boolean
}

class MacdCrossUpTask(exchangeManager: ExchangeManager) : MacdTask(exchangeManager) {
    override fun verify(series: TimeSeries, params: PairExtendedParams): Boolean {
        val closePrice = ClosePriceIndicator(series)
        val macdIndicator = MACDIndicator(closePrice, 9, 26)
        val emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        val macd = macdIndicator.getValue(series.tickCount - 1).toDouble()
        val emaMacd = emaMacdIndicator.getValue(series.tickCount - 1).toDouble()
        val macdHist = macd - emaMacd
        val prevMacd = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdEma = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdHist = prevMacd - prevMacdEma
        val passed = (prevMacdHist < 0) && (macdHist > 0)
        println("[${passed.toString().toUpperCase()}] MACD of ${params.currencyPair} crossed up ($prevMacd/$macd)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("macdcrossup")
    }
}

class MacdCrossDownTask(exchangeManager: ExchangeManager) : MacdTask(exchangeManager) {
    override fun verify(series: TimeSeries, params: PairExtendedParams): Boolean {
        val closePrice = ClosePriceIndicator(series)
        val macdIndicator = MACDIndicator(closePrice, 9, 26)
        val emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        val macd = macdIndicator.getValue(series.tickCount - 1).toDouble()
        val emaMacd = emaMacdIndicator.getValue(series.tickCount - 1).toDouble()
        val macdHist = macd - emaMacd
        val prevMacd = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdEma = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdHist = prevMacd - prevMacdEma
        val passed = (prevMacdHist > 0) && (macdHist < 0)
        println("[${passed.toString().toUpperCase()}] MACD of ${params.currencyPair} crossed down ($prevMacd/$macd)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("macdcrossdown")
    }
}

