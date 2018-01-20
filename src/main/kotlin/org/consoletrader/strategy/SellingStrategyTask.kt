package org.consoletrader.strategy

import org.consoletrader.common.AnalyseResult
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.consoletrader.indicators.IndicatorsDataSource
import org.consoletrader.wallet.ListAssetsDataSource
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order
import java.util.*

class SellingStrategyTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("sellingstrategy")
    }

    override fun execute(paramsRaw: String) {
        val minimumValueInDollars = 500
        val rsiTolerance = 5
        val counterCurrencySymbol = "USD"

        ListAssetsDataSource(exchangeManager)
                .createObservable()
                .filter { it.priceInDollars > minimumValueInDollars && it.assetSymbol != counterCurrencySymbol }
                .flatMap {
                    IndicatorsDataSource(exchangeManager, CurrencyPair(it.assetSymbol, counterCurrencySymbol))
                            .createObservable()
                }
                .map {
                    AnalyseResult(it)
                }
                .filter { isOverbought(it, rsiTolerance) }
                .filter { noSellOrderWithinLastTwoHours(it) }
                .subscribe {
                    //sell
                    println(it)
                }
    }

    private fun isOverbought(it: AnalyseResult, rsiTolerance: Int) =
            it.calculateGain() > 5.0 && it.currentRsi > it.bestOverboughtRsi - rsiTolerance

    private fun noSellOrderWithinLastTwoHours(it: AnalyseResult): Boolean {
        val yesterdayMillis = Calendar.getInstance().timeInMillis - 2 * 60 * 60 * 1000
        val yesterday = Date(yesterdayMillis)
        var hasSellOrdersWithinLast2Hours = false
        val tradeHistory = exchangeManager.getLatestTradeHistory(it.pair)
        tradeHistory
                .userTrades
                .filter {
                    it.type == Order.OrderType.ASK && it.timestamp.after(yesterday)
                }
                .forEach {
                    hasSellOrdersWithinLast2Hours = true
                }
        return !hasSellOrdersWithinLast2Hours
    }

}