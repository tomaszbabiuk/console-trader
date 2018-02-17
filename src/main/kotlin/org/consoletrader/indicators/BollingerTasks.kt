package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator

abstract class BollingerTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairExtendedParams>() {
    override fun createDataSource(params: PairExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }

    override fun createParams(paramsRaw: String): PairExtendedParams {
        return PairExtendedParams(paramsRaw)
    }
}

class BollingerAboveTask(exchangeManager: ExchangeManager) : BollingerTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val ema20Indicator = EMAIndicator(closePriceIndicator, 20)
        val sd20Indicator = StandardDeviationIndicator(closePriceIndicator, 20)
        val middleBandIndicator = BollingerBandsMiddleIndicator(ema20Indicator)
        val upBandIndicator = BollingerBandsUpperIndicator(middleBandIndicator, sd20Indicator)

        val closePrice = closePriceIndicator.getValue(data.tickCount - 1).toDouble()
        val upBand = upBandIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = closePrice > upBand
        println("[${passed.toString().toUpperCase()}] Close price of ${params.currencyPair}: $closePrice > $upBand (Bollinger upper band)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bollingerabove")
    }
}

class BollingerBelowTask(exchangeManager: ExchangeManager) : BollingerTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val ema20Indicator = EMAIndicator(closePriceIndicator, 20)
        val sd20Indicator = StandardDeviationIndicator(closePriceIndicator, 20)
        val middleBandIndicator = BollingerBandsMiddleIndicator(ema20Indicator)
        val lowBandIndicator = BollingerBandsLowerIndicator(middleBandIndicator, sd20Indicator)

        val closePrice = closePriceIndicator.getValue(data.tickCount - 1).toDouble()
        val lowBand = lowBandIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = closePrice < lowBand
        println("[${passed.toString().toUpperCase()}] Close price of ${params.currencyPair}: $closePrice < $lowBand (Bollinger lower band)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bollingerbelow")
    }
}