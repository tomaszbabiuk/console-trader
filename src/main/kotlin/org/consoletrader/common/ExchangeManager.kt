package org.consoletrader.common

import org.consoletrader.exchange.CandlesService
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.account.Wallet
import org.knowm.xchange.dto.trade.UserTrades
import java.math.BigDecimal

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
    abstract fun placeStopOrder(pair: CurrencyPair, price: BigDecimal, amount: BigDecimal)
    abstract fun getExchangeWallet(): Wallet
}