package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*
import org.ta4j.core.TimeSeries

abstract class BestRsiConditionBase(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        PairAndDoubleCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val indicatorsData = IndicatorsData(series, params.currencyPair)
        val analyseResult = AnalyseResult(indicatorsData)
        return mapper(analyseResult)
    }

    abstract fun mapper(analyseResult: AnalyseResult): EvaluationResult
}

class BestOverboughtRsiCondition(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        BestRsiConditionBase(exchangeManager, params) {

    override fun mapper(analyseResult: AnalyseResult): EvaluationResult {
        val passed = analyseResult.currentRsi  > analyseResult.bestOverboughtRsi - params.value
        val comment = if (passed) {
            "[TRUE] best overbought RSI of ${params.currencyPair}: ${analyseResult.bestOverboughtRsi} > ${analyseResult.currentRsi} - ${params.value}"
        } else {
            "[FALSE] best overbought RSI of ${params.currencyPair}: ${analyseResult.bestOverboughtRsi} > ${analyseResult.currentRsi} - ${params.value}"
        }

        return EvaluationResult(passed, comment)
    }
}

class BestOversoldRsiCondition(val exchangeManager: ExchangeManager, val params: BestOversoldRsiExtendedParams) :
        Condition {
    private val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)

    override fun buildEvaluator(): Single<EvaluationResult> {
        return dataSource
                .createSingle()
                .map { it.series }
                .map(this::mapToAnalyseResult)
                .map(this::mapToEvaluationResult)
    }

    private fun mapToAnalyseResult(series: TimeSeries): AnalyseResult {
        val indicatorsData = IndicatorsData(series, params.currencyPair)
        return AnalyseResult(indicatorsData)
    }

    private fun mapToEvaluationResult(analyseResult: AnalyseResult): EvaluationResult {
        val gainPassed = analyseResult.calculateGain() > params.minShortGain
        val athLossPassed = analyseResult.calculateAthLoss() > params.minAthLoss
        val rsiPassed = analyseResult.currentRsi < analyseResult.bestOversoldRsi + params.rsiAdvance
        val rising = analyseResult.currentRsi > analyseResult.bestOversoldRsi
        val passed = gainPassed && athLossPassed && rsiPassed && rising
        val comment = StringBuilder()
        comment.appendln("[${rsiPassed.toString().toUpperCase()}] best oversold RSI: ${analyseResult.currentRsi} < ${analyseResult.bestOversoldRsi} + ${params.rsiAdvance}")
        comment.appendln("[${gainPassed.toString().toUpperCase()}] min gain: ${analyseResult.calculateGain()} > ${params.minShortGain}")
        comment.appendln("[${athLossPassed.toString().toUpperCase()}] min ath loss: ${analyseResult.calculateAthLoss()} > ${params.minAthLoss}")
        comment.appendln("[${rising.toString().toUpperCase()}] rising: ${analyseResult.currentRsi} > ${analyseResult.bestOversoldRsi}")

        return EvaluationResult(passed, comment.toString())
    }
}