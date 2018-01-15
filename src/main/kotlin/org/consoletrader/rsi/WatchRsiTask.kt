package org.consoletrader.rsi

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task

abstract class WatchRsiTask(exchangeManager: ExchangeManager, private val successAction: Action) : Task(exchangeManager) {

    override fun execute(paramsRaw: String) {
        val params = WatchRsiExtendedParams(paramsRaw)
        val dataSource = RsiDataSource(exchangeManager, params.currencyPair)
        val presenter = RsiResultPresenter({ compareRsi(it, params.rsi) }, successAction)
        presenter.present(dataSource)
    }

    abstract fun compareRsi(actual: Double, target: Double): Boolean
}

class WatchRsiBelowTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRsiTask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchrsibelow")
    }

    override fun compareRsi(actual: Double, target: Double): Boolean {
        return actual < target
    }
}

class WatchRsiAboveTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRsiTask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchrsiabove")
    }

    override fun compareRsi(actual: Double, target: Double): Boolean {
        return actual > target
    }
}