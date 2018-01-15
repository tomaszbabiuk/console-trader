package org.consoletrader.common

import org.consoletrader.candles.CandlesService
import org.consoletrader.candles.binance.BinanceCandleService
import org.consoletrader.candles.bitfinex.BitfinexCandleService
import org.consoletrader.candles.bitmarket.BitmarketCandleService
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.bitfinex.v1.BitfinexExchange
import org.knowm.xchange.bitmarket.BitMarketExchange

class ExchangeMatcher {
    fun match(name: String, apiKey: String, apiSecret: String): ExchangeManager? {
        val exchange: Exchange?
        val candlesService: CandlesService?
        val exSpec: ExchangeSpecification?

        when (name) {

            "binance" -> {
                exSpec = BinanceExchange().defaultExchangeSpecification
                candlesService = BinanceCandleService()
            }

            "bitfinex" -> {
                exSpec = BitfinexExchange().defaultExchangeSpecification
                candlesService = BitfinexCandleService()
            }

            "bitmarket" -> {
                exSpec = BitMarketExchange().defaultExchangeSpecification
                candlesService = BitmarketCandleService()
            }

            else -> {
                return null
            }
        }

        exSpec?.apiKey = apiKey
        exSpec?.secretKey = apiSecret
        exchange = ExchangeFactory.INSTANCE.createExchange(exSpec)

        return ExchangeManager(exchange, candlesService)
    }
}