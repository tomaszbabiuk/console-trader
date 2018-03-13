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

abstract class BollingerEdgeTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairExtendedParams>() {
    override fun createDataSource(params: PairExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }

    override fun createParams(paramsRaw: String): PairExtendedParams {
        return PairExtendedParams(paramsRaw)
    }
}

class BollingerEdgeAboveTask(exchangeManager: ExchangeManager) : BollingerEdgeTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val ema20Indicator = EMAIndicator(closePriceIndicator, 20)
        val sd20Indicator = StandardDeviationIndicator(closePriceIndicator, 20)
        val middleBandIndicator = BollingerBandsMiddleIndicator(ema20Indicator)
        val upBandIndicator = BollingerBandsUpperIndicator(middleBandIndicator, sd20Indicator)
        val latestTicker = exchangeManager.exchange.marketDataService.getTicker(params.currencyPair)
        val latestAskPrice = latestTicker.ask.toDouble()
        val upBand = upBandIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = latestAskPrice > upBand

        println("[${passed.toString().toUpperCase()}] Latest ASK price of ${params.currencyPair}: $latestAskPrice > $upBand (Bollinger upper band)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bollingerabove")
    }
}

class BollingerEdgeBelowTask(exchangeManager: ExchangeManager) : BollingerEdgeTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairExtendedParams): Boolean {
        val closePriceIndicator = ClosePriceIndicator(data)
        val ema20Indicator = EMAIndicator(closePriceIndicator, 20)
        val sd20Indicator = StandardDeviationIndicator(closePriceIndicator, 20)
        val middleBandIndicator = BollingerBandsMiddleIndicator(ema20Indicator)
        val lowBandIndicator = BollingerBandsLowerIndicator(middleBandIndicator, sd20Indicator)
        val latestTicker = exchangeManager.exchange.marketDataService.getTicker(params.currencyPair)
        val latestAskPrice = latestTicker.ask.toDouble()
        val lowBand = lowBandIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = latestAskPrice < lowBand

        println("[${passed.toString().toUpperCase()}] Latest ASK price of ${params.currencyPair}: $latestAskPrice < $lowBand (Bollinger lower band)")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bollingerbelow")
    }
}