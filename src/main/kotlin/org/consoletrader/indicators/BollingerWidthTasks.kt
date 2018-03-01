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

abstract class BollingerWidthTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairAndDoubleExtendedParams>() {
    override fun createDataSource(params: PairAndDoubleExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }

    override fun createParams(paramsRaw: String): PairAndDoubleExtendedParams {
        return PairAndDoubleExtendedParams(paramsRaw)
    }

    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val ema20Indicator = EMAIndicator(closePriceIndicator, 20)
        val sd20Indicator = StandardDeviationIndicator(closePriceIndicator, 20)
        val middleBandIndicator = BollingerBandsMiddleIndicator(ema20Indicator)
        val upBandIndicator = BollingerBandsUpperIndicator(middleBandIndicator, sd20Indicator)
        val lowBandIndicator = BollingerBandsLowerIndicator(middleBandIndicator, sd20Indicator)
        val upBand = upBandIndicator.getValue(data.tickCount - 1)
        val lowBand = lowBandIndicator.getValue(data.tickCount - 1)
        val width = upBand.minus(lowBand).toDouble()
        return validateBollingerBandWidth(width, params)
    }

    abstract fun validateBollingerBandWidth(width: Double, params: PairAndDoubleExtendedParams): Boolean
}

class BollingerWidthAboveTask(exchangeManager: ExchangeManager) : BollingerWidthTask(exchangeManager) {
    override fun validateBollingerBandWidth(width: Double, params: PairAndDoubleExtendedParams): Boolean {
        val passed = width > params.value
        println("[${passed.toString().toUpperCase()}] Bollinger width of ${params.currencyPair}: $width > ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bollingerwidthabove")
    }
}

class BollingerWidthBelowTask(exchangeManager: ExchangeManager) : BollingerWidthTask(exchangeManager) {
    override fun validateBollingerBandWidth(width: Double, params: PairAndDoubleExtendedParams): Boolean {
        val passed = width < params.value
        println("[${passed.toString().toUpperCase()}] Bollinger width of ${params.currencyPair}: $width < ${params.value}")
        return passed
    }


    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bollingerwidthbelow")
    }
}