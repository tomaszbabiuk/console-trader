package org.consoletrader.indicators

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task

abstract class WatchMACDTask(exchangeManager: ExchangeManager, private val successAction: Action) : Task(exchangeManager) {

    override fun execute(paramsRaw: String) {
        val params = WatchMACDExtendedParams(paramsRaw)
        val dataSource = MACDDataSource(exchangeManager, params.currencyPair)
        val presenter = IndicatorResultPresenter<MACDValue>({ compareMACD(it) }, successAction)
        presenter.present(dataSource)
    }

    abstract fun compareMACD(macd: MACDValue): Boolean
}

class WatchMACDCrossDownTask(exchangeManager: ExchangeManager,
                             successAction: Action) : WatchMACDTask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchmacdcrossdown")
    }

    override fun compareMACD(macd: MACDValue): Boolean {
        return (macd.current > 0) && (macd.previous < 0)
    }
}

class WatchMACDCrossUpTask(exchangeManager: ExchangeManager,
                           successAction: Action) : WatchMACDTask(exchangeManager, successAction) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("watchmacdcrossup")
    }

    override fun compareMACD(macd: MACDValue): Boolean {
        return (macd.current < 0) && (macd.previous > 0)
    }
}