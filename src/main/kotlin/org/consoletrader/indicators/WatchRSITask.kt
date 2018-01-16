package org.consoletrader.indicators

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task

abstract class WatchRSITask(exchangeManager: ExchangeManager, private val successAction: Action) : Task(exchangeManager) {

    override fun execute(paramsRaw: String) {
        val params = WatchRSIExtendedParams(paramsRaw)
        val dataSource = RSIDataSource(exchangeManager, params.currencyPair)
        val presenter = IndicatorResultPresenter<RSIValue>({ compareRsi(it.level, params.rsi) }, successAction)
        presenter.present(dataSource)
    }

    abstract fun compareRsi(actual: Double, target: Double): Boolean
}

class WatchRSIBelowTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRSITask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchrsibelow")
    }

    override fun compareRsi(actual: Double, target: Double): Boolean {
        return actual < target
    }
}

class WatchRSIAboveTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRSITask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchrsiabove")
    }

    override fun compareRsi(actual: Double, target: Double): Boolean {
        return actual > target
    }
}