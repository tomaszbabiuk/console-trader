package org.consoletrader.tax

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairExtendedParams
import org.consoletrader.common.Task

class TaxTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw == "tax"
    }

    override fun execute(paramsRaw: String) {
        val params = PairExtendedParams(paramsRaw);

        Observable
                .just(exchangeManager.getMaximumTradeHistory(params.currencyPair))
                .flatMapIterable { it.userTrades }
                .blockingSubscribe() { println(it) }
    }
}