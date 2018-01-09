package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.domain.account.request.OrderRequest

class ClearAllOrders(apiKey: String, apiSecret: String) : BaseBinanceOrder(apiKey, apiSecret) {
    override fun execute(client: BinanceApiRestClient) {
        val prices = client.allPrices

        val account = client.account
        account.balances
                .filter { it.free.toDouble() > 0.0001 }
                .forEach {
                        val asset = it.asset
                        val pairs = prices.filter { it.symbol.contains(asset) }
                        val orders = pairs
                                .map {
                                    client.getOpenOrders(OrderRequest(it.symbol))
                                }
                                .filter { it.isNotEmpty() }
                                .distinct()

                    if (orders.isEmpty()) {
                        println("No open orders")
                    } else {
                        orders.map { println(it) }
                    }
                }
    }
}