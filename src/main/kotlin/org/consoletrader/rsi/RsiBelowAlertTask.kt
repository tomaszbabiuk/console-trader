package org.consoletrader.rsi

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import java.util.concurrent.TimeUnit


class RsiBelowAlertTask(exchangeManager: ExchangeManager, private val pair: CurrencyPair, private val rsi: Double) : Task {
    private val candleService = exchangeManager.candlesService

    override fun execute() {
        Observable
                .interval(0, 5, TimeUnit.MINUTES)
                .subscribe { doRsiLoop() }
    }

    private fun doRsiLoop() {
        val series = BaseTimeSeries("rsi_ticks")

        candleService
                .getCandles(pair)
                .doOnComplete {
                    val closePrice = ClosePriceIndicator(series)
                    series.tickCount
                    val rsiIndicator = RSIIndicator(closePrice, 14)
                    val rsiValue = rsiIndicator.getValue(series.tickCount - 1).toDouble()
                    println(rsiValue)
                }
                .subscribe{
                    series.addTick(it)
                }
    }
}