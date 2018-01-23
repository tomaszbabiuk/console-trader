package org.consoletrader.profit

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairExtendedParams
import org.consoletrader.common.Task
import org.knowm.xchange.dto.Order

class CalculateStopPriceTask(val exchangeManager: ExchangeManager) : Task {
    private val accountService = exchangeManager.exchange.accountService

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("calculatestop")
    }

    override fun execute(paramsRaw: String) {
        val params = PairExtendedParams(paramsRaw)
        var totalBuy = 0.0
        var totalFee = 0.0
        var totalSell = 0.0
        var currentAmount = 0.0
        var marketPrice = 0.0
        Observable
                .just(accountService.accountInfo.wallet.getBalance(params.currencyPair.base))
                .doOnNext { currentAmount = it.total.toDouble() }
                .map { exchangeManager.exchange.marketDataService.getTicker(params.currencyPair) }
                .doOnNext { marketPrice = it.high.toDouble() }
                .flatMapIterable {
                    exchangeManager.getMaximumTradeHistory(params.currencyPair).userTrades
                }
                .doOnNext {
                    val fee = if (it.feeCurrency == params.currencyPair.base) {
                        it.feeAmount.toDouble() * it.price.toDouble()
                    } else {
                        it.feeAmount.toDouble()
                    }

                    totalFee += fee
                    if (it.type == Order.OrderType.BID) {
                        totalBuy += it.originalAmount.toDouble() * it.price.toDouble() - fee
                    } else {
                        totalSell += it.originalAmount.toDouble() * it.price.toDouble() - fee
                    }
                }
                .doOnComplete {
                    val buyingCost = totalBuy + totalFee - totalSell
                    val total = currentAmount * marketPrice - buyingCost
                    val stopPrice = total/currentAmount
                    println("Trades buy total: $totalBuy")
                    println("Trades sell total: $totalSell")
                    println("Trades fees: $totalFee")
                    println("Trades cost: $buyingCost")
                    println("Current amount: $currentAmount${params.currencyPair.base}")
                    println("Current market price: $marketPrice")
                    println("==================================")
                    println("Total: $total")
                    println("Suggested STOP price: $stopPrice${params.currencyPair.counter}")
                }
                .blockingSubscribe()
    }
}