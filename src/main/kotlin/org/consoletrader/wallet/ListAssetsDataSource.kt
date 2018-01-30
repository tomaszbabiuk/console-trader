package org.consoletrader.wallet

import io.reactivex.Observable
import io.reactivex.Single
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.DataSource
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService
import java.math.BigDecimal

open class ListAssetsDataSource(exchangeManager: ExchangeManager) : DataSource<MutableList<PortfolioAsset>> {
    private val marketDataService: MarketDataService = exchangeManager.exchange.marketDataService
    private val accountService = exchangeManager.exchange.accountService
    private var etherPrice: Double? = null

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

    override fun create(): Single<MutableList<PortfolioAsset>> {
        return Observable
                .just(accountService.accountInfo)
                .flatMapIterable { it.wallet.balances.values }
                .filter { it.total > BigDecimal.ZERO}
                .map {
                    val usd = calculateAssetPrice(it.currency.toString(), it.total.toDouble())
                    PortfolioAsset(it.currency.symbol, it.total.toDouble(), usd)
                }
                .toList()
    }
}
