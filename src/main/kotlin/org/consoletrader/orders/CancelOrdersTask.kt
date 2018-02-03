package org.consoletrader.orders

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairExtendedParams
import org.consoletrader.common.Task

class CancelOrdersTask(exchangeManager: ExchangeManager) : Task {
    val tradingService = exchangeManager.exchange.tradeService

    override fun execute(paramsRaw: String) {
        val params = PairExtendedParams(paramsRaw)
        tradingService.openOrders.openOrders.forEach {
            if (it.currencyPair == params.currencyPair) {
                println("Cancelling order $it")
                tradingService.cancelOrder(it.id)
            }
        }
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("cancelorders")
    }

}