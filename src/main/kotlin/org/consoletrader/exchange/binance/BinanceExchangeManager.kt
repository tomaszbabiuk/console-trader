package org.consoletrader.exchange.binance

import org.consoletrader.common.ExchangeManager
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.binance.service.BinanceTradeHistoryParams
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.trade.UserTrades

class BinanceExchangeManager(apiKey: String, apiSecret: String) :
        ExchangeManager(BinanceExchange().defaultExchangeSpecification, apiKey, apiSecret, BinanceCandleService()) {

    override fun getLatestTradeHistory(pair: CurrencyPair): UserTrades {
        val tradeParams = BinanceTradeHistoryParams()
        tradeParams.currencyPair = pair
        tradeParams.limit = 30
        return exchange.tradeService.getTradeHistory(tradeParams)
    }
}