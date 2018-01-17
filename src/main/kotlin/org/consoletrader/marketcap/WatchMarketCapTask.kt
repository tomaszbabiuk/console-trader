package org.consoletrader.marketcap

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.consoletrader.marketcap.coinmarketcap.CoinmarketcapMarketcapService

class WatchMarketCapTask(exchangeManager: ExchangeManager, private val successAction: Action) : Task(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcap")
    }

    override fun execute(paramsRaw: String) {
        val datasource = CoinmarketcapMarketcapService()
        val presenter = WatchMarketcapDataPresenter(this::isCompleted, successAction)
        presenter.present(datasource)
    }

    fun isCompleted(current:Marketcap, previous:Marketcap?): Boolean {
        var delta = Marketcap(0.0)
        if (previous != null) {
            delta = Marketcap(current.value - previous.value)
        }
        println("marketcap=$current, delta=$delta")
        return false
    }
}
