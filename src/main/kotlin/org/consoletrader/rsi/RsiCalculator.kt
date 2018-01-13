package org.consoletrader.rsi

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Calculator
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator


class RsiCalculator(exchangeManager: ExchangeManager, private val pair: CurrencyPair) : Calculator<Double> {
    private val candleService = exchangeManager.candlesService

    override fun calculate(): Observable<Double> {
        return candleService
                .getCandles(pair)
                .map {
                    val series = BaseTimeSeries("rsi_ticks", it)
                    val closePrice = ClosePriceIndicator(series)
                    val rsiIndicator = RSIIndicator(closePrice, 14)
                    rsiIndicator.getValue(series.tickCount - 1).toDouble()
                }
                .toObservable()
    }
}