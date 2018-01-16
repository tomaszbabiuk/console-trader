package org.consoletrader.indicators

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.DataSource
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

data class MACDValue(val current: Double, val previous: Double) {
    override fun toString(): String {
        return "MACD: $current/$previous"
    }
}

class MACDDataSource(exchangeManager: ExchangeManager, private val pair: CurrencyPair) : DataSource<MACDValue> {
    private val candleService = exchangeManager.candlesService

    override fun createObservable(): Observable<MACDValue> {
        return candleService
                .getCandles(pair)
                .map {
                    val series = BaseTimeSeries("macd_ticks", it)
                    val closePrice = ClosePriceIndicator(series)
                    val macdIndicator = MACDIndicator(closePrice, 9,26)
                    val current = macdIndicator.getValue(series.tickCount -1).toDouble()
                    val previous = macdIndicator.getValue(series.tickCount -2).toDouble()
                    MACDValue(current, previous)
                }
                .toObservable()
    }
}