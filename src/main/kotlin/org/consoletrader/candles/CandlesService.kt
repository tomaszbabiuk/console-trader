package org.consoletrader.candles

import io.reactivex.Observable
import org.knowm.xchange.currency.CurrencyPair

interface CandlesService {
    fun getCandles(pair: CurrencyPair): Observable<Candle>
}