package org.consoletrader.profit

import io.reactivex.Single
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.DataSource
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.account.Balance
import org.knowm.xchange.dto.marketdata.Ticker
import org.knowm.xchange.dto.trade.UserTrades
import io.reactivex.functions.Function3


class ProfitDataSource(private val exchangeManager: ExchangeManager, private val pair: CurrencyPair, private val calculatedAmount: Double) : DataSource<Profit> {
    private val accountService = exchangeManager.exchange.accountService

    private fun calculateProfitFromLastTrades(balance: Balance, ticker: Ticker, trades: UserTrades): Profit {
        var iteratedCost = 0.0

        var currentAmount = balance.total.toDouble()
        if (calculatedAmount < currentAmount) {
            currentAmount = calculatedAmount
        }

        var amountLeft = currentAmount

        trades
                .userTrades
                .reversed()
                .filter { it.type == Order.OrderType.BID }
                .takeWhile { amountLeft != 0.0 }
                .forEach {
                    val feeCurrency = if (it.feeCurrency == pair.base) {
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

    override fun create(): Single<Profit> {

        val walletObservable = Single.just(accountService.accountInfo.wallet.getBalance(pair.base))
        val tickerObservable = Single.just(exchangeManager.exchange.marketDataService.getTicker(pair))
        val tradesObservable = Single.just(exchangeManager.getMaximumTradeHistory(pair))

        return Single.zip(
                walletObservable,
                tickerObservable,
                tradesObservable,
                Function3(this::calculateProfitFromLastTrades))
    }
}