package org.consoletrader.exchange.bitfinex

import org.consoletrader.common.ExchangeManager
import org.knowm.xchange.bitfinex.v1.BitfinexExchange
import org.knowm.xchange.bitfinex.v1.service.BitfinexTradeService
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.trade.UserTrades
import java.util.*

class BitfinexExchangeManager(apiKey: String, apiSecret: String) :
        ExchangeManager(BitfinexExchange().defaultExchangeSpecification, apiKey, apiSecret, BitfinexCandleService()) {

    override fun getLatestTradeHistory(pair: CurrencyPair): UserTrades {
        val millis = Calendar.getInstance().timeInMillis - 24 * 60 * 60 * 1000
        return getTradeHistory(pair, millis)
    }

    override fun getMaximumTradeHistory(pair: CurrencyPair): UserTrades {
        return getTradeHistory(pair, 0)
    }

    private fun getTradeHistory(pair: CurrencyPair, timestamp: Long): UserTrades {
        val startDate = Date(timestamp)
        val tradeParams = BitfinexTradeService.BitfinexTradeHistoryParams(startDate, 30, pair)
        return exchange.tradeService.getTradeHistory(tradeParams)
    }
}