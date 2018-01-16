package org.consoletrader.indicators

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.DataSource
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

data class RSIValue(val level: Double) {
    override fun toString(): String {
        return "RSI: $level"
    }
}

class RSIDataSource(exchangeManager: ExchangeManager, private val pair: CurrencyPair) : DataSource<RSIValue> {
    private val candleService = exchangeManager.candlesService

    override fun createObservable(): Observable<RSIValue> {
        return candleService
                .getCandles(pair)
                .map {
                    val series = BaseTimeSeries("rsi_ticks", it)
                    val closePrice = ClosePriceIndicator(series)
                    val rsiIndicator = RSIIndicator(closePrice, 14)
                    val rsiValue = rsiIndicator.getValue(series.tickCount - 1).toDouble()
                    RSIValue(rsiValue)
                }
                .toObservable()
    }
}