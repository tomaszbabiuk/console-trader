package org.consoletrader.wallet

import org.consoletrader.common.Task
import org.knowm.xchange.Exchange
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService
import java.math.BigDecimal

open class ListAssetsTask(exchange: Exchange) : Task {
    private val marketDataService: MarketDataService = exchange.marketDataService
    private val accountService = exchange.accountService
    private var etherPrice: Double? = null

    override fun execute() {
        val accountInfo = accountService.accountInfo
        var usdSum = 0.0
        for (x in accountInfo.wallet.balances) {
            if (x.value.total > BigDecimal.ZERO) {
                val usd = calculateAssetPrice(x.value.currency.toString(), x.value.total.toDouble())
                val asset = PortfolioAsset(x.value.currency.symbol, x.value.total.toDouble(), usd)
                println(asset)
                usdSum += usd
            }
        }
        println("USD sum is $usdSum")
    }

    private fun calculateAssetPrice(symbol: String, amount: Double): Double {
        val usdCurrencyPair = CurrencyPair(symbol, "USD")
        val usdValue = tryGetTickerData(usdCurrencyPair, amount)
        if (usdValue != null) {
            return usdValue
        }

        val usdtCurrencyPair = CurrencyPair(symbol, "USDT")
        val usdtResult = tryGetTickerData(usdtCurrencyPair, amount)
        if (usdtResult != null) {
            return usdtResult
        }

        val ethCurrencyPair = CurrencyPair(symbol, "ETH")
        val ethResult = tryGetTickerData(ethCurrencyPair, amount)
        if (ethResult != null) {
            if (etherPrice == null) {
                etherPrice = calculateAssetPrice("ETH", 1.0)
            }

            if (etherPrice != null) {
                return ethResult * etherPrice!!
            }
        }

        return 0.0
    }


    private fun tryGetTickerData(pair: CurrencyPair, amount: Double): Double? {
        try {
            val ticker = marketDataService.getTicker(pair)
            return ticker.last.toDouble() * amount
        } catch (ignored: Exception) {
        }

        return null
    }
}
