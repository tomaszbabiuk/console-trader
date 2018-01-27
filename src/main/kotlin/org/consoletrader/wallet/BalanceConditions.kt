package org.consoletrader.wallet

import io.reactivex.Single
import org.consoletrader.common.Condition
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager

open class BalanceAboveCondition(exchangeManager: ExchangeManager, val params: CurrencyAndValueExtendedParams) : Condition {
    private val listAssetsDataSource = ListAssetsDataSource(exchangeManager)

    override fun buildEvaluator(): Single<EvaluationResult> {
        return listAssetsDataSource.
                createObservable()
                .filter {
                    it.assetSymbol == params.currency.symbol
                }
                .toList()
                .flatMap {
                    if (it.size == 1) {
                        val balance = it[0].amount
                        val passed = check(balance)
                        val comment = "[${passed.toString().toUpperCase()}] Wallet balance of ${params.currency}: ${balance} > ${params.value}"
                        val result = EvaluationResult(passed, comment)
                        Single.just(result)
                    } else {
                        val result = EvaluationResult(false, "[FALSE} There's no record about ${params.currency} on the wallet")
                        Single.just(result)
                    }
                }
    }

    protected open fun check(balance:Double) : Boolean {
        return  balance >= params.value
    }
}

class BalanceBelowCondition(exchangeManager: ExchangeManager, params: CurrencyAndValueExtendedParams) : BalanceAboveCondition(exchangeManager, params) {
    override fun check(balance: Double): Boolean {
        return balance <= params.value
    }
}