package org.consoletrader.exchange.kucoin

//import org.consoletrader.common.ExchangeManager
//import org.knowm.xchange.bitfinex.v1.BitfinexExchange
//import org.knowm.xchange.bitfinex.v1.service.BitfinexTradeService
//import org.knowm.xchange.currency.CurrencyPair
//import org.knowm.xchange.dto.trade.UserTrades
//import org.knowm.xchange.kucoin.KucoinExchange
//import java.util.*

//class KuCoinExchangeManager(apiKey: String, apiSecret: String) :
//        ExchangeManager(KucoinExchange().defaultExchangeSpecification, apiKey, apiSecret, KuCoinCandleService()) {
//
//    override fun getLatestTradeHistory(pair: CurrencyPair): UserTrades {
//        val millis = Calendar.getInstance().timeInMillis - 24 * 60 * 60 * 1000
//        return getTradeHistory(pair, millis)
//    }
//
//    override fun getMaximumTradeHistory(pair: CurrencyPair): UserTrades {
//        return getTradeHistory(pair, 0)
//    }
//
//    private fun getTradeHistory(pair: CurrencyPair, timestamp: Long): UserTrades {
//        val startDate = Date(timestamp)
//        val tradeParams = BitfinexTradeService.BitfinexTradeHistoryParams(startDate, 30, pair)
//        return exchange.tradeService.getTradeHistory(tradeParams)
//    }
//}