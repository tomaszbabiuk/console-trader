package org.consoletrader.exchange

import io.reactivex.Single
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.Tick

interface CandlesService {
    fun getCandles(pair: CurrencyPair): Single<MutableList<Tick>>
}