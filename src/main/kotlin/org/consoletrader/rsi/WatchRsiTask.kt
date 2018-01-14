package org.consoletrader.rsi

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task

abstract class WatchRsiTask(exchangeManager: ExchangeManager, private val successAction: Action) : Task(exchangeManager) {

    override fun execute(taskRaw: String) {
        val params = WatchRsiTaskParams(taskRaw)
        val calculator = RsiCalculator(exchangeManager, params.currencyPair)
        val presenter = RsiResultPresenter({ compareRsi(it, params.rsi) }, successAction)
        presenter.present(calculator)
    }

    abstract fun compareRsi(actual: Double, target: Double): Boolean
}

class WatchRsiBelowTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRsiTask(exchangeManager, successAction) {
    override fun match(taskRaw: String): Boolean {
        return taskRaw.startsWith("watchrsibelow")
    }

    override fun compareRsi(actual: Double, target: Double): Boolean {
        return actual < target
    }
}

class WatchRsiAboveTask(exchangeManager: ExchangeManager,
                        successAction: Action) : WatchRsiTask(exchangeManager, successAction) {
    override fun match(taskRaw: String): Boolean {
        return taskRaw.startsWith("watchrsiabove")
    }

    override fun compareRsi(actual: Double, target: Double): Boolean {
        return actual > target
    }
}