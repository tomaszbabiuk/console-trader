package org.consoletrader.profit

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import kotlin.system.exitProcess

class MinProfitTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("minprofit")
    }

    override fun execute(paramsRaw: String) {
        val params = ProfitExtendedParams(paramsRaw)
        val profitDataSource = ProfitDataSource(exchangeManager, params.currencyPair, params.countedAmount)

        profitDataSource
                .create()
                .doOnSuccess {
                    println(it)

                    val result = checkProfit(it, params)
                    if (result) {
                        exitProcess(0)
                    } else {
                        exitProcess(1)
                    }
                }
                .doOnError {
                    println(it)
                    exitProcess(1)
                }
                .blockingGet()

    }

    private fun checkProfit(profit: Profit, params: ProfitExtendedParams): Boolean {
        val passed = profit.returnOfInvestment > params.roi
        println("[${passed.toString().toUpperCase()}] Roi of ${params.currencyPair}/${params.countedAmount}: ${profit.returnOfInvestment} > ${params.roi}")
        return passed
    }

}
