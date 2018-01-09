package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.domain.account.AssetBalance
import com.binance.api.client.domain.account.request.OrderRequest
import com.binance.api.client.domain.market.TickerPrice
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.consoletrader.market.Order

class OpenOrders(apiKey: String, apiSecret: String) : MyPortfolio(apiKey, apiSecret) {

    override fun execute(client: BinanceApiRestClient) {
        val balanceObservable = buildPositiveBalanceObservable(client)
        val pricesObservable = Observable.just(client.allPrices)

        balanceObservable
                .zipWith(pricesObservable, BiFunction { balance: List<AssetBalance>, price: List<TickerPrice> -> Pair(balance, price) })
                .flatMapIterable(this::extractTradingPairsRelatedToAccount)
                .distinct()
                .flatMapIterable { client.getOpenOrders(OrderRequest(it)) }
                .doOnComplete { println("DONE") }
                .map { Order(it.symbol, it.origQty.toDouble(), it.price.toDouble(), it.status.toString(), it.type.toString(), it.side.toString()) }
                .subscribe { println(it) }
    }

    private fun extractTradingPairsRelatedToAccount(pair: Pair<List<AssetBalance>, List<TickerPrice>>): ArrayList<String> {
        val result = ArrayList<String>()
        val balances = pair.first
        val prices = pair.second

        for (currency in balances) {
            val assetPrice = calculateAssetPrice(currency, prices)
            if (assetPrice > 10) {
                val marketsToCheckBalance = pair.second
                        .filter { it.symbol.contains(currency.asset) }
                        .map { it.symbol }
                result.addAll(marketsToCheckBalance)
            }
        }

        return result
    }
}