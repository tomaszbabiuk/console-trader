package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiRestClient
import org.consoletrader.market.Order

class OpenOrdersTask(apiKey: String, apiSecret: String) : BaseBinanceOrderTask(apiKey, apiSecret) {
    override fun processOrder(client: BinanceApiRestClient, order: Order) {
        println { order }
    }
}