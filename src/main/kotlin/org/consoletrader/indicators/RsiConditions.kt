package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

abstract class RsiCondition(exchangeManager: ExchangeManager, val params: RsiExtendedParams) : Condition {
    private val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)

    override fun buildEvaluator(): Single<EvaluationResult> {
        return dataSource
                .createSingle()
                .map(this::mapper)
                .onErrorResumeNext { throwable -> Single.just(EvaluationResult(false, "Exception: $throwable")) }
    }

    abstract fun mapper(series: TimeSeries): EvaluationResult
}

class RsiAboveCondition(exchangeManager: ExchangeManager, params: RsiExtendedParams) :
        RsiCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val closePrice = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePrice, 14)
        val rsi = rsiIndicator.getValue(series.tickCount - 1)
        val ruleResult = rsi.toDouble() > params.rsi
        val comment = if (ruleResult) {
            "[TRUE] RSI of ${params.currencyPair}=$rsi > ${params.rsi}"
        } else {
            "[FALSE] RSI of ${params.currencyPair}=$rsi < ${params.rsi}"
        }
        return EvaluationResult(ruleResult, comment)
    }
}

class RsiBelowCondition(exchangeManager: ExchangeManager, params: RsiExtendedParams) :
        RsiCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val closePrice = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePrice, 14)
        val rsi = rsiIndicator.getValue(series.tickCount - 1)
        val ruleResult = rsi.toDouble() < params.rsi
        val comment = if (ruleResult) {
            "[TRUE] RSI of ${params.currencyPair}: $rsi < ${params.rsi}"
        } else {
            "[FALSE] RSI of ${params.currencyPair}: $rsi > ${params.rsi}"
        }
        return EvaluationResult(ruleResult, comment)
    }
}