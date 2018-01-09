package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.domain.account.AssetBalance
import com.binance.api.client.domain.market.TickerPrice
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.consoletrader.market.PortfolioAsset

open class ListAssetsTask(apiKey: String, apiSecret: String) : BaseBinanceTask(apiKey, apiSecret) {

    override fun execute(client: BinanceApiRestClient) {
        buildPortfolioObservable(client)
                .doOnComplete { println("DONE") }
                .subscribe { println(it) }
    }

    protected fun buildPositiveBalanceObservable(client: BinanceApiRestClient): Observable<List<AssetBalance>> {
        return Observable
                .just(client.account.balances)
                .flatMapIterable { it }
                .filter { it.free.toDouble() > 0 }
                .toList()
                .toObservable()
    }

    private fun buildPortfolioObservable(client: BinanceApiRestClient): Observable<PortfolioAsset> {
        val balancesObservable = buildPositiveBalanceObservable(client)
        val pricesObservable = Observable.just(client.allPrices)

        return balancesObservable
                .zipWith(pricesObservable, BiFunction { balance: List<AssetBalance>, price: List<TickerPrice> -> Pair(balance, price) })
                .flatMapIterable(this::extractTradingPairsRelatedToAccount)
    }

    private fun extractTradingPairsRelatedToAccount(pair: Pair<List<AssetBalance>, List<TickerPrice>>): List<PortfolioAsset> {
        val balances = pair.first
        val prices = pair.second
        val result = ArrayList<PortfolioAsset>()

        for (currency in balances) {
            val assetPrice = calculateAssetPrice(currency, prices)
            val portfolio = PortfolioAsset(currency.asset, currency.free.toDouble() + currency.locked.toDouble(), assetPrice)
            result.add(portfolio)
        }

        return result
    }

    protected fun calculateAssetPrice(assetBalance: AssetBalance, prices: List<TickerPrice>): Double {
        val usdtMarket = "${assetBalance.asset}USDT"
        val canBePricedInDollars = prices.map { it.symbol }.contains(usdtMarket)
        if (canBePricedInDollars) {
            val usdtPrice = prices.first { it.symbol == usdtMarket }.price.toDouble()
            return assetBalance.free.toDouble() * usdtPrice
        }

        val etherMarket = "${assetBalance.asset}ETH"
        val canBePricedInEther = prices.map { it.symbol }.contains(etherMarket)
        if (canBePricedInEther) {
            val etherPrice = prices.first { it.symbol == "ETHUSDT" }.price.toDouble()
            val assetPrice = prices.first { it.symbol == etherMarket }.price.toDouble()
            return assetBalance.free.toDouble() * assetPrice * etherPrice
        }

        //TODO:use binance coin as well
        return 0.0
    }
}