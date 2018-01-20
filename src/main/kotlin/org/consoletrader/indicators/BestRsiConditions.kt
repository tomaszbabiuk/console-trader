package org.consoletrader.indicators

import org.consoletrader.common.AnalyseResult
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
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

class BestOversoldRsiCondition(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        BestRsiConditionBase(exchangeManager, params) {

    override fun mapper(analyseResult: AnalyseResult): EvaluationResult {
        val passed = analyseResult.currentRsi < analyseResult.bestOversoldRsi + params.value
        val comment = if (passed) {
            "[TRUE] best oversold RSI of ${params.currencyPair}: ${analyseResult.currentRsi} < ${analyseResult.bestOversoldRsi} + ${params.value}"
        } else {
            "[FALSE] best oversold RSI of ${params.currencyPair}: ${analyseResult.currentRsi} < ${analyseResult.bestOversoldRsi
            } + ${params.value}"
        }

        return EvaluationResult(passed, comment)
    }
}