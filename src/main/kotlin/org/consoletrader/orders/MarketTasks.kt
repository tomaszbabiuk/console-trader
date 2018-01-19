package org.consoletrader.orders

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.MarketOrder
import java.math.BigDecimal
import java.math.RoundingMode

abstract class MarketTask(val exchangeManager: ExchangeManager, private val orderType: Order.OrderType) : Task {
    override fun execute(paramsRaw: String) {
        val params = MarketExtendedParams(paramsRaw)
        val tradeService = exchangeManager.exchange.tradeService
        if (params.amountCurrency == params.currencyPair.counter) {
            val tickerPrice = exchangeManager.exchange.marketDataService.getTicker(params.currencyPair).bid * BigDecimal.valueOf(1.05)
            val amountToBuy: BigDecimal = params.amountValue.divide(tickerPrice, 2, RoundingMode.DOWN)
            println("Trying to buy $amountToBuy${params.currencyPair.base} for about ${params.amountValue}${params.currencyPair.counter} ")
            tradeService.placeMarketOrder(MarketOrder(orderType, amountToBuy, params.currencyPair))
        } else {
            println("Trying to buy $params.amountValue${params.currencyPair.base}")
            tradeService.placeMarketOrder(MarketOrder(orderType, params.amountValue, params.currencyPair))
        }
    }
}

class MarketBuyTask(exchangeManager: ExchangeManager) : MarketTask(exchangeManager, Order.OrderType.BID) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketbuy")
    }
}

class MarketSellTask(exchangeManager: ExchangeManager) : MarketTask(exchangeManager, Order.OrderType.ASK) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketsell")
    }
}