package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
import org.ta4j.core.TimeSeries

abstract class PairAndDoubleCondition(exchangeManager: ExchangeManager, val params: PairAndDoubleExtendedParams) : Condition {
    private val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)

    override fun buildEvaluator(): Single<EvaluationResult> {
        return dataSource
                .createSingle()
                .map(this::mapper)
                .onErrorResumeNext { throwable -> Single.just(EvaluationResult(false, "Exception: $throwable")) }
    }

    abstract fun mapper(series: TimeSeries): EvaluationResult
}