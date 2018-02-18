package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*
import org.ta4j.core.TimeSeries

abstract class MfiTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairAndDoubleExtendedParams>() {
    override fun createParams(paramsRaw: String): PairAndDoubleExtendedParams {
        return PairAndDoubleExtendedParams(paramsRaw)
    }

    override fun createDataSource(params: PairAndDoubleExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }

    }
}

class MfiAboveTask(exchangeManager: ExchangeManager) : MfiTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val mfiIndicator = MoneyFlowIndicator(data, 14)
        val mfi = mfiIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = mfi > params.value
        println("[${passed.toString().toUpperCase()}] MFI of ${params.currencyPair}: $mfi > ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("mfiabove")
    }
}

class MfiBelowTask(exchangeManager: ExchangeManager) : MfiTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val mfiIndicator = MoneyFlowIndicator(data, 14)
        val mfi = mfiIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = mfi < params.value
        println("[${passed.toString().toUpperCase()}] MFI of ${params.currencyPair}: $mfi < ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("mfibelow")
    }
}
