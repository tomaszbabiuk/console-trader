package org.consoletrader.roi

import io.reactivex.Single
import org.consoletrader.common.DataSourceTask
import org.consoletrader.common.ExchangeManager

abstract class RoiTask(val exchangeManager: ExchangeManager) : DataSourceTask<Profit, RoiExtendedParams>() {
    override fun createDataSource(params: RoiExtendedParams): Single<Profit> {
        return ProfitDataSource(exchangeManager, params.currencyPair, params.countedAmount).create()
    }

    override fun createParams(paramsRaw: String): RoiExtendedParams {
        return RoiExtendedParams(paramsRaw)
    }
}

class RoiAboveTask(exchangeManager: ExchangeManager) : RoiTask(exchangeManager) {
    override fun verifySuccess(data: Profit, params: RoiExtendedParams): Boolean {
        val passed = data.returnOfInvestment > params.roi
        println("[${passed.toString().toUpperCase()}] Roi of ${params.currencyPair}/${params.countedAmount}: ${data.returnOfInvestment} > ${params.roi}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("roiabove")
    }
}

class RoiBelowTask(exchangeManager: ExchangeManager) : RoiTask(exchangeManager) {
    override fun verifySuccess(data: Profit, params: RoiExtendedParams): Boolean {
        val passed = data.returnOfInvestment < params.roi
        println("[${passed.toString().toUpperCase()}] Roi of ${params.currencyPair}/${params.countedAmount}: ${data.returnOfInvestment} < ${params.roi}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("roibelow")
    }
}