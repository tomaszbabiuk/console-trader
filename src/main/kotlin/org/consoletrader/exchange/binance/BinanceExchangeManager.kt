package org.consoletrader.exchange.binance

import org.consoletrader.common.ExchangeManager
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.binance.service.BinanceTradeHistoryParams
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.trade.UserTrades

class BinanceExchangeManager(apiKey: String, apiSecret: String) :
        ExchangeManager(BinanceExchange().defaultExchangeSpecification, apiKey, apiSecret, BinanceCandleService()) {
    override fun getMaximumTradeHistory(pair: CurrencyPair): UserTrades {
        return getTradeHistory(pair, 100)
    }

    override fun getLatestTradeHistory(pair: CurrencyPair): UserTrades {
        return getTradeHistory(pair, 30)
    }

    private fun getTradeHistory(pair: CurrencyPair, limit: Int) : UserTrades {
        val tradeParams = BinanceTradeHistoryParams()
        tradeParams.currencyPair = pair
        tradeParams.limit = limit
        return exchange.tradeService.getTradeHistory(tradeParams)
    }
}