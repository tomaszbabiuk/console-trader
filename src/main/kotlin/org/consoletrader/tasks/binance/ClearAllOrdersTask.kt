package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiRestClient
import org.consoletrader.market.Order
import com.binance.api.client.domain.account.request.CancelOrderRequest



class ClearAllOrdersTask(apiKey: String, apiSecret: String) : BaseBinanceOrderTask(apiKey, apiSecret) {
    override fun processOrder(client: BinanceApiRestClient, order: Order) {
        client.cancelOrder(CancelOrderRequest(order.symbol, order.id))
    }

}