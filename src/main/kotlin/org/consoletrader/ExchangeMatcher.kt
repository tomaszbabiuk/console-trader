package org.consoletrader

import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.bitfinex.v1.BitfinexExchange

class ExchangeMatcher {
    fun match(name: String, apiKey: String, apiSecret: String): Exchange? {
        val exSpec = when (name) {
            "binance" -> BinanceExchange().defaultExchangeSpecification
            "bitfinex" -> BitfinexExchange().defaultExchangeSpecification
            else -> null
        }

        if (exSpec != null) {
            exSpec.apiKey = apiKey
            exSpec.secretKey = apiSecret
            return ExchangeFactory.INSTANCE.createExchange(exSpec)
        }

        return null
    }
}