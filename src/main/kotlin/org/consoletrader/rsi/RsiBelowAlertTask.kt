package org.consoletrader.rsi

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.Tick
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import java.util.*
import java.util.concurrent.TimeUnit


class RsiBelowAlertTask(exchangeManager: ExchangeManager,
                        private val pair: CurrencyPair,
                        private val rsi: Double,
                        private val timeFrame: Int = 14) : Task {
    private val candleService = exchangeManager.candlesService


    override fun execute() {
        Observable
                .interval(0, 5, TimeUnit.MINUTES)
                .flatMap { doRsiLoop() }
                .subscribe()
    }

    private fun doRsiLoop(): Observable<Double> {
        return candleService
                .getCandles(pair)
                .toObservable()
                .onErrorResumeNext(Observable.empty<MutableList<Tick>>())
                .map {
                    val series = BaseTimeSeries("rsi_ticks", it)
                    val closePrice = ClosePriceIndicator(series)
                    val rsiIndicator = RSIIndicator(closePrice, timeFrame)
                    rsiIndicator.getValue(series.tickCount - 1).toDouble()
                }
                .doOnNext {
                    println("${Date()}, RSI: $it")
                }
    }
}