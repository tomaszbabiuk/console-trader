package org.consoletrader.strategy

import io.reactivex.Observable
import org.consoletrader.common.AnalyseResult
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.consoletrader.indicators.IndicatorsDataSource
import org.consoletrader.common.PairsExtendedParams

class BuyingStrategyTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("analyse")
    }

    override fun execute(paramsRaw: String) {
        val params = PairsExtendedParams(paramsRaw)
        Observable
                .just(params.currencyPairs)
                .flatMapIterable {
                    it
                }
                .flatMap {
                    IndicatorsDataSource(exchangeManager, it).createObservable()
                }
                .map {
                    AnalyseResult(it)
                }
                .subscribe {
                    println(it)
                    println()
                }
    }
}