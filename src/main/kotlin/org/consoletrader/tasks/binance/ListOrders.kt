package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiRestClient
import com.binance.api.client.domain.account.AssetBalance
import com.binance.api.client.domain.market.TickerPrice
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class ListOrders(apiKey: String, apiSecret: String) : BaseBinanceOrder(apiKey, apiSecret) {

    override fun execute(client: BinanceApiRestClient) {
        val balancesObservable = Observable
                .just(client.account.balances)
                .flatMapIterable { it }
                .filter { it.free.toDouble() > 0 }

        val pricesObservable = Observable.just(client.allPrices).flatMapIterable { it }

        balancesObservable
                .zipWith(pricesObservable, BiFunction { balance: AssetBalance, price: TickerPrice -> Pair(balance, price) })
                .subscribe {
                    println { it }
                }

//                .map { client.getOpenOrders(OrderRequest(it.asset)) }
//                .subscribe {
//                    println {it}
//                }


//        account.balances
//                .filter { it.free.toDouble() > 0 }
//                .forEach {
//                    val asset = it.asset
//                    val pairs = prices.filter { it.symbol.contains(asset) }
//                    val orders = pairs.map { client.getOpenOrders(OrderRequest(it.symbol)) }
//
//                    //orders.forEach { ordersList.add(it) }
//                    if (orders.isEmpty()) {
//                        orders.map { println(it) }
//                    }
//                }
    }

    private fun convert2(b: Any): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}