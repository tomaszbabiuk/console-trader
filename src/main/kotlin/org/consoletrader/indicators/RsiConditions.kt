package org.consoletrader.indicators

import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

abstract class RsiConditionBase(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        PairAndDoubleCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val closePrice = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePrice, 14)
        val rsi = rsiIndicator.getValue(series.tickCount - 1).toDouble()
        return mapper(rsi)
    }

    abstract fun mapper(rsi: Double): EvaluationResult
}

class RsiAboveCondition(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        RsiConditionBase(exchangeManager, params) {

    override fun mapper(rsi: Double): EvaluationResult {
        val passed = rsi > params.value
        val comment = if (passed) {
            "[TRUE] RSI of ${params.currencyPair}: $rsi > ${params.value}"
        } else {
            "[FALSE] RSI of ${params.currencyPair}: $rsi > ${params.value}"
        }

        return EvaluationResult(passed, comment)
    }
}

class RsiBelowCondition(exchangeManager: ExchangeManager, params: PairAndDoubleExtendedParams) :
        RsiConditionBase(exchangeManager, params) {

    override fun mapper(rsi: Double): EvaluationResult {
        val passed = rsi < params.value
        val comment = if (passed) {
            "[TRUE] RSI of ${params.currencyPair}: $rsi < ${params.value}"
        } else {
            "[FALSE] RSI of ${params.currencyPair}: $rsi < ${params.value}"
        }
        return EvaluationResult(passed, comment)
    }
}