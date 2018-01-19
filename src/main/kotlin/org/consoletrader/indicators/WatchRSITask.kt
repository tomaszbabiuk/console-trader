package org.consoletrader.indicators

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.ta4j.core.Decimal
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule


abstract class WatchRSITask(val exchangeManager: ExchangeManager, private val successAction: Action) : Task {

    override fun execute(paramsRaw: String) {
        val params = WatchRSIExtendedParams(paramsRaw)
        val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)
        val presenter = WatchPeriodicDataPresenter<TimeSeries>({ compareRsi(it, params.rsi) }, successAction)
        presenter.present(dataSource)
    }

    abstract fun compareRsi(series: TimeSeries, targetRsi: Double): Boolean
}

class WatchRSIBelowTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRSITask(exchangeManager, successAction) {

    override fun compareRsi(series: TimeSeries, targetRsi: Double): Boolean {
        val closePrice = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePrice, 14)
        return CrossedDownIndicatorRule(rsiIndicator, Decimal.valueOf(targetRsi)).isSatisfied(series.tickCount - 1)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchrsibelow")
    }
}

class WatchRSIAboveTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRSITask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchrsiabove")
    }

    override fun compareRsi(series: TimeSeries, targetRsi: Double): Boolean {
        val closePrice = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePrice, 14)
        return CrossedUpIndicatorRule(rsiIndicator, Decimal.valueOf(targetRsi)).isSatisfied(series.tickCount - 1)
    }
}