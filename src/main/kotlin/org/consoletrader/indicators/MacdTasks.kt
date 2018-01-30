package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

abstract class MacdTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairExtendedParams>() {
    override fun createParams(paramsRaw: String): PairExtendedParams {
        return PairExtendedParams(paramsRaw)
    }

    override fun createDataSource(params: PairExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }
}

class MacdCrossUpTask(exchangeManager: ExchangeManager) : MacdTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairExtendedParams): Boolean {
        val closePrice = ClosePriceIndicator(data)
        val macdIndicator = MACDIndicator(closePrice, 9, 26)
        val emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        val macd = macdIndicator.getValue(data.tickCount - 1).toDouble()
        val emaMacd = emaMacdIndicator.getValue(data.tickCount - 1).toDouble()
        val macdHist = macd - emaMacd
        val prevMacd = macdIndicator.getValue(data.tickCount - 2).toDouble()
        val prevMacdEma = macdIndicator.getValue(data.tickCount - 2).toDouble()
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
    override fun verifySuccess(data: TimeSeries, params: PairExtendedParams): Boolean {
        val closePrice = ClosePriceIndicator(data)
        val macdIndicator = MACDIndicator(closePrice, 9, 26)
        val emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        val macd = macdIndicator.getValue(data.tickCount - 1).toDouble()
        val emaMacd = emaMacdIndicator.getValue(data.tickCount - 1).toDouble()
        val macdHist = macd - emaMacd
        val prevMacd = macdIndicator.getValue(data.tickCount - 2).toDouble()
        val prevMacdEma = macdIndicator.getValue(data.tickCount - 2).toDouble()
        val prevMacdHist = prevMacd - prevMacdEma
        val passed = (prevMacdHist > 0) && (macdHist < 0)
        println("[${passed.toString().toUpperCase()}] MACD of ${params.currencyPair} crossed down ($prevMacd/$macd)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("macdcrossdown")
    }
}

