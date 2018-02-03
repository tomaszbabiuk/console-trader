package org.consoletrader.orders

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairExtendedParams
import org.consoletrader.common.Task
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.MarketOrder
import java.math.BigDecimal
import java.math.RoundingMode

abstract class OrderTask(val exchangeManager: ExchangeManager) : Task {

    override fun execute(paramsRaw: String) {
        val params = OrderExtendedParams(paramsRaw)
        placeOrder(params)
    }

    protected fun calculateAmount(params: OrderExtendedParams): BigDecimal {
        return if (params.amountCurrency == params.currencyPair.counter) {
            val tickerPrice = exchangeManager.exchange.marketDataService.getTicker(params.currencyPair).bid * BigDecimal.valueOf(1.05)
            val amountToBuy: BigDecimal = params.amountValue.divide(tickerPrice, 2, RoundingMode.DOWN)
            amountToBuy
        } else {
            params.amountValue
        }
    }

    abstract fun placeOrder(params: OrderExtendedParams)
}

class MarketBuyTask(exchangeManager: ExchangeManager) : OrderTask(exchangeManager) {
    private val tradeService = exchangeManager.exchange.tradeService!!

    override fun placeOrder(params: OrderExtendedParams) {
        val order = MarketOrder(Order.OrderType.BID, calculateAmount(params), params.currencyPair)
        println("Placing market buy order $order")
        tradeService.placeMarketOrder(order)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketbuy")
    }
}

class MarketSellTask(exchangeManager: ExchangeManager) : OrderTask(exchangeManager) {
    private val tradeService = exchangeManager.exchange.tradeService!!

    override fun placeOrder(params: OrderExtendedParams) {
        val order = MarketOrder(Order.OrderType.ASK, calculateAmount(params), params.currencyPair)
        println("Placing market sell order $order")
        tradeService.placeMarketOrder(order)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketsell")
    }
}

class TrailingStopTask(exchangeManager: ExchangeManager) :  OrderTask(exchangeManager) {
    override fun placeOrder(params: OrderExtendedParams) {
        exchangeManager.placeStopOrder(params.currencyPair, params.price.toBigDecimal(), calculateAmount(params))
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("trailingstop")
    }
}