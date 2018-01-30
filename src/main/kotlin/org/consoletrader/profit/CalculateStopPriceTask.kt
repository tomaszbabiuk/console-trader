package org.consoletrader.profit

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams
import org.consoletrader.common.Task

data class Profit(val averageBuyingPrice: Double, val currentPrice: Double, val returnOfInvestment: Double)

class CalculateStopPriceTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("calculatestop")
    }

    override fun execute(paramsRaw: String) {
        val params = PairAndDoubleExtendedParams(paramsRaw)
        val dataSource = ProfitDataSource(exchangeManager, params.currencyPair, params.value)

        dataSource
                .create()
                .doOnSuccess {
                    println(it)
                }
                .blockingGet()
    }
}