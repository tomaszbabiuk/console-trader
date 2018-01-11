package org.consoletrader.common

import org.consoletrader.candles.CandlesService
import org.consoletrader.candles.binance.BinanceCandleService
import org.consoletrader.candles.bitfinex.BitfinexCandleService
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.bitfinex.v1.BitfinexExchange

class ExchangeMatcher {
    fun match(name: String, apiKey: String, apiSecret: String): ExchangeManager? {
        val exchange: Exchange?
        val candlesService: CandlesService?
        var exSpec: ExchangeSpecification? = null

        when (name) {
            "binance" -> {
                exSpec = BinanceExchange().defaultExchangeSpecification
                candlesService = BinanceCandleService()
            }

            "bitfinex" -> {
                exSpec = BitfinexExchange().defaultExchangeSpecification
                candlesService = BitfinexCandleService()
            }

            else -> {
                return null
            }
        }

        exSpec.apiKey = apiKey
        exSpec.secretKey = apiSecret
        exchange = ExchangeFactory.INSTANCE.createExchange(exSpec)

        return ExchangeManager(exchange, candlesService)
    }
}