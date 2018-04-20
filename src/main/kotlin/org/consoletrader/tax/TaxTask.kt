package org.consoletrader.tax

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairExtendedParams
import org.consoletrader.common.Task
import org.knowm.xchange.dto.Order
import java.math.BigDecimal

class TaxTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("tax")
    }

    override fun execute(paramsRaw: String) {
        val params = PairExtendedParams(paramsRaw);

        var fifo = ArrayList<FiFoItem>()
        var totalIncome = BigDecimal.ZERO
        var totalCost = BigDecimal.ZERO
        Observable
                .just(exchangeManager.getMaximumTradeHistory(params.currencyPair))
                .flatMapIterable { it.userTrades }
                .doOnComplete {
                    println("Total cost: $totalCost ${params.currencyPair.counter}")
                    println("Total income: $totalIncome ${params.currencyPair.counter}")
                    println("Tax base: ${totalIncome-totalCost} ${params.currencyPair.counter}")
                }
                .subscribe {
                    if (it.type == Order.OrderType.BID) {
                        val toFifo = FiFoItem(it.originalAmount, it.price)
//                        val toFifo = FiFoItem(0.8.toBigDecimal(), it.price)
                        fifo.add(toFifo)

                        println("Adding to FIFO:\n$it\n")
                    }

                    if (it.type == Order.OrderType.ASK) {
                        var amountLeft = it.originalAmount
                        while (amountLeft > BigDecimal.ZERO) {
                            if (fifo.size == 0) {
                                println("FiFo is empty -> more assets is sold than bought ???")
                                break
                            }

                            val first = fifo[0]
                            val firstItemExceeded = first.amount <= it.originalAmount
                            val amountToSubtract = if (firstItemExceeded) {
                                first.amount
                            } else {
                                it.originalAmount
                            }

                            if (firstItemExceeded) {
                                fifo.removeAt(0)
                            }

                            val cost = first.price * amountToSubtract
                            val income = it.price * amountToSubtract
                            first.amount = first.amount.subtract(amountToSubtract)

                            totalCost += cost
                            totalIncome += income
                            println("Removing $amountToSubtract from FIFO:\n$it")
                            println("Cost: $cost, income: $income\n")
                            amountLeft -= amountToSubtract
                        }
                    }
                }
    }
}

class FiFoItem(var amount: BigDecimal, val price: BigDecimal)