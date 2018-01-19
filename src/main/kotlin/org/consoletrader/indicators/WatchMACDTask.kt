package org.consoletrader.indicators

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.ta4j.core.TimeSeries
import org.ta4j.core.trading.rules.UnderIndicatorRule
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.trading.rules.OverIndicatorRule


abstract class WatchMACDTask(val exchangeManager: ExchangeManager, private val successAction: Action) : Task {

    override fun execute(paramsRaw: String) {
        val params = PairOnlyExtendedParams(paramsRaw)
        val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)
        val presenter = WatchPeriodicDataPresenter<TimeSeries>({ checkIfRuleIsSatisfied(it) }, successAction)
        presenter.present(dataSource)
    }

    abstract fun checkIfRuleIsSatisfied(series: TimeSeries): Boolean
}

class WatchMACDCrossDownTask(exchangeManager: ExchangeManager,
                             successAction: Action) : WatchMACDTask(exchangeManager, successAction) {

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchmacdcrossdown")
    }

    override fun checkIfRuleIsSatisfied(series: TimeSeries): Boolean {
        val closePrice = ClosePriceIndicator(series)
        val macd = MACDIndicator(closePrice, 9, 26)
        val emaMacd = EMAIndicator(macd, 18)
        return UnderIndicatorRule(macd, emaMacd).isSatisfied(series.tickCount - 1)
    }
}

class WatchMACDCrossUpTask(exchangeManager: ExchangeManager,
                           successAction: Action) : WatchMACDTask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchmacdcrossup")
    }

    override fun checkIfRuleIsSatisfied(series: TimeSeries): Boolean {
        val closePrice = ClosePriceIndicator(series)
        val macd = MACDIndicator(closePrice, 9, 26)
        val emaMacd = EMAIndicator(macd, 18)
        return OverIndicatorRule(macd, emaMacd).isSatisfied(series.tickCount - 1)
    }
}