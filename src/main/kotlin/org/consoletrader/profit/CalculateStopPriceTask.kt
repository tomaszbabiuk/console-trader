package org.consoletrader.profit

import io.reactivex.Single
import io.reactivex.functions.Function3
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams
import org.consoletrader.common.Task
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.account.Balance
import org.knowm.xchange.dto.marketdata.Ticker
import org.knowm.xchange.dto.trade.UserTrades

data class Profit(val averageBuyingPrice: Double, val currentPrice: Double, val returnOfInvestment: Double)

class CalculateStopPriceTask(val exchangeManager: ExchangeManager) : Task {
    private val accountService = exchangeManager.exchange.accountService

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("calculatestop")
    }

    private var params: PairAndDoubleExtendedParams? = null

    private fun calculateProfitFromLastTrades(balance: Balance, ticker: Ticker, trades: UserTrades): Profit {
        var iteratedCost = 0.0

        var currentAmount = balance.total.toDouble()
        if (params!!.value < currentAmount) {
            currentAmount = params!!.value
        }

        var amountLeft = currentAmount

        trades
                .userTrades
                .reversed()
                .filter { it.type == Order.OrderType.BID }
                .takeWhile { amountLeft != 0.0 }
                .forEach {
                    val feeCurrency = if (it.feeCurrency == params!!.currencyPair.base) {
                        it.feeAmount.toDouble()
                    } else {
                        0.0
                    }

                    if (it.type == Order.OrderType.BID) {
                        if (amountLeft > 0) {
                            if (it.originalAmount.toDouble() < amountLeft) {

                                amountLeft -= it.originalAmount.toDouble() - feeCurrency
                                iteratedCost += it.price.toDouble() * it.originalAmount.toDouble()
                            } else {
                                iteratedCost += it.price.toDouble() * amountLeft
                                amountLeft = 0.0
                            }
                        }
                    }
                }

        val marketPrice = ticker.last.toDouble()
        val averagePrice = iteratedCost / currentAmount
        val roi = 100 - averagePrice / marketPrice * 100
        return Profit(averagePrice, marketPrice, roi)
    }

    override fun execute(paramsRaw: String) {
        params = PairAndDoubleExtendedParams(paramsRaw)

        val currencyPair = params!!.currencyPair
        val walletObservable = Single.just(accountService.accountInfo.wallet.getBalance(currencyPair.base))
        val tickerObservable = Single.just(exchangeManager.exchange.marketDataService.getTicker(currencyPair))
        val tradesObservable = Single.just(exchangeManager.getMaximumTradeHistory(currencyPair))

        Single.zip(
                walletObservable,
                tickerObservable,
                tradesObservable,
                Function3(this::calculateProfitFromLastTrades))
                .doOnSuccess {
                    println(it)
                }
                .blockingGet()
    }
}