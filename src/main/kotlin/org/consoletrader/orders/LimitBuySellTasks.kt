package org.consoletrader.orders

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
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
        when (params.amountUnit) {
            OrderExtendedParams.AmountUnit.CounterCurrency -> {
                val tickerPrice = exchangeManager.exchange.marketDataService.getTicker(params.currencyPair).bid * BigDecimal.valueOf(1.05)
                return params.amountValue.divide(tickerPrice, 2, RoundingMode.DOWN)
            }
            OrderExtendedParams.AmountUnit.Percent -> {
                val balance = exchangeManager
                        .exchange
                        .accountService
                        .accountInfo
                        .wallet
                        .balances
                        .getOrDefault(params.currencyPair.base, null)

                if (balance != null) {
                    return balance.available.multiply(params.amountValue).divide(100.toBigDecimal())
                }

                return 0.0.toBigDecimal()
            }
            else -> {
                return params.amountValue
            }
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