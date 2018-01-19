package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

abstract class MacdCondition(exchangeManager: ExchangeManager, val params: PairOnlyExtendedParams) : Condition {
    private val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)

    override fun buildEvaluator(): Single<EvaluationResult> {
        return dataSource
                .createSingle()
                .map(this::mapper)
                .onErrorResumeNext { throwable -> Single.just(EvaluationResult(false, "Exception: $throwable")) }
    }

    abstract fun mapper(series: TimeSeries): EvaluationResult
}

class MacdCrossUpCondition(exchangeManager: ExchangeManager, params: PairOnlyExtendedParams) :
        MacdCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        val closePrice = ClosePriceIndicator(series)
        val macdIndicator = MACDIndicator(closePrice, 9, 26)
        val emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        val macd = macdIndicator.getValue(series.tickCount - 1).toDouble()
        val emaMacd = emaMacdIndicator.getValue(series.tickCount - 1).toDouble()
        val macdHist = macd - emaMacd
        val prevMacd = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdEma = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdHist = prevMacd - prevMacdEma
        val passed = (prevMacdHist < 0) && (macdHist > 0)
        val comment = if (passed) {
            "[TRUE] MACD of ${params.currencyPair} crossed up ($prevMacd/$macd)"
        } else {
            "[FALSE] MACD of ${params.currencyPair} didn't cross up ($prevMacd/$macd)"
        }
        return EvaluationResult(passed, comment)
    }
}

class MacdCrossDownCondition(exchangeManager: ExchangeManager, params: PairOnlyExtendedParams) :
        MacdCondition(exchangeManager, params) {

    override fun mapper(series: TimeSeries): EvaluationResult {
        //TODO: reuse part of MacdCrossUpCondition
        val closePrice = ClosePriceIndicator(series)
        val macdIndicator = MACDIndicator(closePrice, 9, 26)
        val emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        val macd = macdIndicator.getValue(series.tickCount - 1).toDouble()
        val emaMacd = emaMacdIndicator.getValue(series.tickCount - 1).toDouble()
        val macdHist = macd - emaMacd
        val prevMacd = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdEma = macdIndicator.getValue(series.tickCount - 2).toDouble()
        val prevMacdHist = prevMacd - prevMacdEma
        val passed = (prevMacdHist > 0) && (macdHist < 0)
        val comment = if (passed) {
            "[TRUE] MACD of ${params.currencyPair} crossed down ($prevMacd/$macd)"
        } else {
            "[FALSE] MACD of ${params.currencyPair} didn't cross down ($prevMacd/$macd)"
        }
        return EvaluationResult(passed, comment)
    }
}

