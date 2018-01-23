package org.consoletrader.common

import org.consoletrader.exchange.CandlesService
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.trade.UserTrades

abstract class ExchangeManager(spec: ExchangeSpecification,
                               apiKey: String,
                               apiSecret: String,
                               val candlesService: CandlesService) {

    var exchange: Exchange

    init {
            spec.apiKey = apiKey
            spec.secretKey = apiSecret
            exchange = ExchangeFactory.INSTANCE.createExchange(spec)
        }

    abstract fun getLatestTradeHistory(pair: CurrencyPair): UserTrades

    abstract fun getMaximumTradeHistory(pair: CurrencyPair): UserTrades
}