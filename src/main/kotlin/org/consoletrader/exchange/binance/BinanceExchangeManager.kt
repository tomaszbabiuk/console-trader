package org.consoletrader.exchange.binance

import org.consoletrader.common.ExchangeManager
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.binance.service.BinanceTradeHistoryParams
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.account.Wallet
import org.knowm.xchange.dto.trade.UserTrades
import java.math.BigDecimal
import javax.ws.rs.NotSupportedException

class BinanceExchangeManager(apiKey: String, apiSecret: String) :
        ExchangeManager(BinanceExchange().defaultExchangeSpecification, apiKey, apiSecret, BinanceCandleService()) {
    override fun placeStopOrder(pair: CurrencyPair, price: BigDecimal, amount: BigDecimal) {
        throw NotSupportedException("Trailing stop orders are not supported for BINANCE exchange")
    }

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

    override fun getExchangeWallet(): Wallet {
        return exchange.accountService.accountInfo.wallet
    }
}
