package org.consoletrader.exchange

import org.consoletrader.common.ExchangeManager
import org.consoletrader.exchange.binance.BinanceExchangeManager
import org.consoletrader.exchange.bitfinex.BitfinexExchangeManager
import org.consoletrader.exchange.kucoin.KuCoinExchangeManager


class ExchangeMatcher {
    fun match(name: String, apiKey: String, apiSecret: String): ExchangeManager? {
        when (name) {

            "binance" -> {
                return BinanceExchangeManager(apiKey, apiSecret)
            }

            "bitfinex" -> {
                return BitfinexExchangeManager(apiKey, apiSecret)
            }

            "kucoin" -> {
                return KuCoinExchangeManager(apiKey, apiSecret)
            }

            else -> {
                return null
            }
        }
    }
}