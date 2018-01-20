package org.consoletrader.exchange

import org.consoletrader.common.ExchangeManager
import org.consoletrader.exchange.binance.BinanceExchangeManager
import org.consoletrader.exchange.bitfinex.BitfinexExchangeManager

class ExchangeMatcher {
    fun match(name: String, apiKey: String, apiSecret: String): ExchangeManager? {
        when (name) {

            "binance" -> {
                return BinanceExchangeManager(apiKey, apiSecret)
            }

            "bitfinex" -> {
                return BitfinexExchangeManager(apiKey, apiSecret)
            }

//            "bitmarket" -> {
//                exSpec = BitMarketExchange().defaultExchangeSpecification
//                candlesService = BitmarketCandleService()
//            }

            else -> {
                return null
            }
        }
    }
}