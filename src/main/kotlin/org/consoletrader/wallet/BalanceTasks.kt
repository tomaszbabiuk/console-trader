package org.consoletrader.wallet

import io.reactivex.Single
import org.consoletrader.common.EvaluationResult
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import kotlin.system.exitProcess

abstract class BalanceTask(exchangeManager: ExchangeManager) : Task {
    private val listAssetsDataSource = ListAssetsDataSource(exchangeManager)

    override fun execute(paramsRaw: String) {
        val params = CurrencyAndValueExtendedParams(paramsRaw)

        listAssetsDataSource.
                createObservable()
                .filter {
                    it.assetSymbol == params.currency.symbol
                }
                .toList()
                .doOnSuccess {
                    if (it.size == 1) {
                        print(it)

                        val balance = it[0].amount
                        val result = verifyBalance(balance, params.value)
                        if (result) {
                            exitProcess(0)
                        } else {
                            exitProcess(1)
                        }
                    } else {
                        val result = EvaluationResult(false, "[FALSE} There's no record about ${params.currency} on the wallet")
                        Single.just(result)
                    }
                }
                .doOnError {
                    println(it)
                    exitProcess(1)
                }
                .blockingGet()
    }

    abstract fun verifyBalance(balance: Double, threshold: Double): Boolean
}

class BalanceAboveTask(exchangeManager: ExchangeManager) : BalanceTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("balanceabove")
    }

    override fun verifyBalance(balance: Double, threshold: Double): Boolean {
        return balance > threshold
    }
}

class BalanceBelowTask(exchangeManager: ExchangeManager) : BalanceTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("balancebelow")
    }

    override fun verifyBalance(balance: Double, threshold: Double): Boolean {
        return balance < threshold
    }
}