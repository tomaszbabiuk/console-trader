package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*

abstract class BestRsiTask(val exchangeManager: ExchangeManager) : DataSourceTask<AnalyseResult, BestOversoldRsiExtendedParams>() {
    override fun createDataSource(params: BestOversoldRsiExtendedParams): Single<AnalyseResult> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }
                .map {
                    val indicatorsData = IndicatorsData(it, params.currencyPair)
                    AnalyseResult(indicatorsData)
                }
    }

    override fun createParams(paramsRaw: String): BestOversoldRsiExtendedParams {
        return BestOversoldRsiExtendedParams(paramsRaw)
    }
}


class BestOversoldRsiTask(exchangeManager: ExchangeManager) : BestRsiTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bestoversoldrsi")
    }

    override fun verifySuccess(data: AnalyseResult, params: BestOversoldRsiExtendedParams): Boolean {
        val gainPassed = data.calculateGain() > params.minShortGain
        val athLossPassed = data.calculateAthLoss() > params.minAthLoss
        val rsiPassed = data.currentRsi < data.bestOversoldRsi + params.rsiAdvance
        val rising = data.currentRsi > data.bestOversoldRsi
        val passed = gainPassed && athLossPassed && rsiPassed && rising
        println("[${rsiPassed.toString().toUpperCase()}] best oversold RSI: ${data.currentRsi} < ${data.bestOversoldRsi} + ${params.rsiAdvance}")
        println("[${gainPassed.toString().toUpperCase()}] min gain: ${data.calculateGain()} > ${params.minShortGain}")
        println("[${athLossPassed.toString().toUpperCase()}] min ath loss: ${data.calculateAthLoss()} > ${params.minAthLoss}")
        println("[${rising.toString().toUpperCase()}] rising: ${data.currentRsi} > ${data.bestOversoldRsi}")

        return passed
    }
}

class BestOverboughtRsiTask(exchangeManager: ExchangeManager) : BestRsiTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bestoverboughtrsi")
    }

    override fun verifySuccess(data: AnalyseResult, params: BestOversoldRsiExtendedParams): Boolean {
        val passed = data.currentRsi > data.bestOverboughtRsi - params.rsiAdvance
        println("[${passed.toString().toUpperCase()}] best overbought RSI of ${params.currencyPair}: ${data.currentRsi} > ${data.bestOverboughtRsi} - ${params.rsiAdvance}")
        return passed
    }
}