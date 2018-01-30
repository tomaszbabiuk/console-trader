package org.consoletrader.profit

import io.reactivex.Single
import org.consoletrader.common.DataSourceTask
import org.consoletrader.common.ExchangeManager

class MinProfitTask(val exchangeManager: ExchangeManager) : DataSourceTask<Profit, ProfitExtendedParams>() {
    override fun verifySuccess(data: Profit, params: ProfitExtendedParams): Boolean {
        val passed = data.returnOfInvestment > params.roi
        println("[${passed.toString().toUpperCase()}] Roi of ${params.currencyPair}/${params.countedAmount}: ${data.returnOfInvestment} > ${params.roi}")
        return passed
    }

    override fun createDataSource(params: ProfitExtendedParams): Single<Profit> {
        return ProfitDataSource(exchangeManager, params.currencyPair, params.countedAmount).create()
    }

    override fun createParams(paramsRaw: String): ProfitExtendedParams {
        return ProfitExtendedParams(paramsRaw)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("minprofit")
    }
}