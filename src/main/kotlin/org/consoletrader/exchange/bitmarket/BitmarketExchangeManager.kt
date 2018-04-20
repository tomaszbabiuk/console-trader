package org.consoletrader.exchange.bitmarket

import org.consoletrader.common.ExchangeManager
import org.knowm.xchange.bitfinex.v1.service.BitfinexTradeService
import org.knowm.xchange.bitmarket.BitMarketExchange
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.account.Wallet
import org.knowm.xchange.dto.trade.UserTrades
import java.math.BigDecimal
import java.util.*
import javax.ws.rs.NotSupportedException

class BitmarketExchangeManager(apiKey: String, apiSecret: String) :
        ExchangeManager(BitMarketExchange().defaultExchangeSpecification, apiKey, apiSecret, BitmarketCandleService()) {

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

    override fun placeStopOrder(pair: CurrencyPair, price: BigDecimal, amount: BigDecimal) {
        throw NotSupportedException("Trailing stop orders are not supported for KUCOIN exchange")
    }

    override fun getExchangeWallet(): Wallet {
        return exchange.accountService.accountInfo.wallet
    }
}
