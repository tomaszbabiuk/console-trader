package org.consoletrader.indicators

import org.consoletrader.common.*
import kotlin.system.exitProcess

abstract class BestRsiTask(val exchangeManager: ExchangeManager) : Task {

    override fun execute(paramsRaw: String) {
        val params = BestOversoldRsiExtendedParams(paramsRaw)
        val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)
        dataSource
                .createSingle()
                .map { it.series }
                .map {
                    val indicatorsData = IndicatorsData(it, params.currencyPair)
                    AnalyseResult(indicatorsData)
                }
                .doOnSuccess {
                    println(it)

                    val result = verify(it, params)
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

    abstract fun verify(analyseResult: AnalyseResult, params: BestOversoldRsiExtendedParams): Boolean
}


class BestOversoldRsiTask(exchangeManager: ExchangeManager) : BestRsiTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bestoversoldrsi")
    }

    override fun verify(analyseResult: AnalyseResult, params: BestOversoldRsiExtendedParams): Boolean {
        val gainPassed = analyseResult.calculateGain() > params.minShortGain
        val athLossPassed = analyseResult.calculateAthLoss() > params.minAthLoss
        val rsiPassed = analyseResult.currentRsi < analyseResult.bestOversoldRsi + params.rsiAdvance
        val rising = analyseResult.currentRsi > analyseResult.bestOversoldRsi
        val passed = gainPassed && athLossPassed && rsiPassed && rising
        println("[${rsiPassed.toString().toUpperCase()}] best oversold RSI: ${analyseResult.currentRsi} < ${analyseResult.bestOversoldRsi} + ${params.rsiAdvance}")
        println("[${gainPassed.toString().toUpperCase()}] min gain: ${analyseResult.calculateGain()} > ${params.minShortGain}")
        println("[${athLossPassed.toString().toUpperCase()}] min ath loss: ${analyseResult.calculateAthLoss()} > ${params.minAthLoss}")
        println("[${rising.toString().toUpperCase()}] rising: ${analyseResult.currentRsi} > ${analyseResult.bestOversoldRsi}")

        return passed
    }
}

class BestOverboughtRsiTask(exchangeManager: ExchangeManager) : BestRsiTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bestoverboughtrsi")
    }

    override fun verify(analyseResult: AnalyseResult, params: BestOversoldRsiExtendedParams): Boolean {
        val passed = analyseResult.currentRsi > analyseResult.bestOverboughtRsi - params.rsiAdvance
        println("[${passed.toString().toUpperCase()}] best overbought RSI of ${params.currencyPair}: ${analyseResult.currentRsi} > ${analyseResult.bestOverboughtRsi} - ${params.rsiAdvance}")
        return passed
    }
}