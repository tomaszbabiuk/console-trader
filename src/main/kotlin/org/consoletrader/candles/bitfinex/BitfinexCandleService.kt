package org.consoletrader.candles.bitfinex

import io.reactivex.Observable
import org.consoletrader.candles.Candle
import org.consoletrader.candles.CandlesService
import org.consoletrader.candles.base.BaseApi
import org.knowm.xchange.currency.CurrencyPair

class BitfinexCandleService : BaseApi<BitfinexPublicAPI>(
        anApi = BitfinexPublicAPI::class.java,
        endpoint = "https://api.bitfinex.com"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Observable<Candle> {
        return getApi()
                .queryCandles("${pair.base}${pair.counter}")
                .flatMapIterable { it }
                .map {
                    val timestamp = it[0].toLong()
                    val open = it[1]
                    val close = it[2]
                    val high = it[3]
                    val low = it[4]
                    val volume = it[5]
                    Candle(timestamp, open, close, high, low, volume)
                }
    }

}

