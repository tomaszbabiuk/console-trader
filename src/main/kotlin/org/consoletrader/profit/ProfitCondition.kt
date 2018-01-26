package org.consoletrader.profit

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager

class ProfitCondition(exchangeManager: ExchangeManager, val params: ProfitExtendedParams) : Condition {
    private val profitDataSource = ProfitDataSource(exchangeManager, params.currencyPair, params.countedAmount)

    override fun buildEvaluator(): Single<EvaluationResult> {
        return profitDataSource
                .createSingle()
                .map(this::mapper)
                .onErrorResumeNext { throwable -> Single.just(EvaluationResult(false, "Exception: $throwable")) }

    }

    private fun mapper(profit:Profit): EvaluationResult {
        val passed = profit.returnOfInvestment > params.roi
        val comment = "[${passed.toString().toUpperCase()}] Roi of ${params.currencyPair}/${params.countedAmount}: ${profit.returnOfInvestment} > ${params.roi}"
        return EvaluationResult(passed, comment)
    }
}
