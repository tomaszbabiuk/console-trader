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
        var currentAmount = 0.0
        var marketPrice = 0.0
        var iteratedCost = 0.0
        var amountLeft = 0.0
        Observable
                .just(accountService.accountInfo.wallet.getBalance(params.currencyPair.base))
                .doOnNext {
                    currentAmount = it.total.toDouble()
                    amountLeft = currentAmount
                }
                .map { exchangeManager.exchange.marketDataService.getTicker(params.currencyPair) }
                .doOnNext {
                    marketPrice = it.last.toDouble()
                }
                .flatMapIterable {
                    exchangeManager.getMaximumTradeHistory(params.currencyPair).userTrades.reversed()
                }
                .filter { it.type == Order.OrderType.BID }
                .takeUntil {
                    amountLeft == 0.0
                }
                .doOnNext {
                    if (amountLeft > 0) {
                        if (it.originalAmount.toDouble() < amountLeft) {
                            val feeAmount = if (it.feeCurrency == params.currencyPair.base) {
                                it.feeAmount.toDouble()
                            } else {
                                0.0
                            }
                            amountLeft -= it.originalAmount.toDouble() - feeAmount
                            iteratedCost += it.price.toDouble() * it.originalAmount.toDouble()
                        } else {
                            iteratedCost += it.price.toDouble() * amountLeft
                            amountLeft = 0.0
                        }
                    }
                }
                .doOnComplete {
                    val averagePrice = iteratedCost / currentAmount
                    val roi = 100 - averagePrice/marketPrice * 100
                    println("Average buying price is: $averagePrice")
                    println("Current price is: $marketPrice")
                    println("Return of investment is: $roi%")
                }
                .blockingSubscribe {
                    println(it)
                }
    }
}