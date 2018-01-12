package org.consoletrader.candles

import io.reactivex.Observable
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.Tick

interface CandlesService {
    fun getCandles(pair: CurrencyPair): Observable<Tick>
}