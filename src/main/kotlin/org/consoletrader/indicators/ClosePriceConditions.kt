package org.consoletrader.indicators

import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class ClosePriceAboveCondition(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        PairAndDoubleCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val closePriceIndicator = ClosePriceIndicator(series)
        val closePrice = closePriceIndicator.getValue(series.tickCount - 1).toDouble()
        val passed = closePrice > params.value
        val comment = if (passed) {
            "[TRUE] Close price of ${params.currencyPair}: $closePrice > ${params.value}"
        } else {
            "[FALSE] Close price of ${params.currencyPair}: $closePrice > ${params.value}"
        }
        return EvaluationResult(passed, comment)
    }
}

class ClosePriceBelowCondition(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        PairAndDoubleCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val closePriceIndicator = ClosePriceIndicator(series)
        val closePrice = closePriceIndicator.getValue(series.tickCount - 1).toDouble()
        val passed = closePrice < params.value
        val comment = if (passed) {
            "[TRUE] Close price of ${params.currencyPair}: $closePrice < ${params.value}"
        } else {
            "[FALSE] Close price of ${params.currencyPair}: $closePrice < ${params.value}"
        }
        return EvaluationResult(passed, comment)
    }
}