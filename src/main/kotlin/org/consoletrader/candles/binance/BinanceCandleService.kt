package org.consoletrader.candles.binance

import io.reactivex.Observable
import org.consoletrader.candles.Candle
import org.consoletrader.candles.CandlesService
import org.consoletrader.candles.base.BaseApi
import org.knowm.xchange.currency.CurrencyPair

class BinanceCandleService : BaseApi<BinancePublicAPI>(
        anApi = BinancePublicAPI::class.java,
        endpoint = "https://api.binance.com"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Observable<Candle> {
        return getApi()
                .queryCandles("${pair.base}${pair.counter}")
                .flatMapIterable { it }
                .map {
                    val open = it[1].toDouble()
                    val high = it[2].toDouble()
                    val low = it[3].toDouble()
                    val close = it[3].toDouble()
                    val volume = it[5].toDouble()
                    val timestamp = it[6].toLong()
                    Candle(timestamp, open, close, high, low, volume)
                }
    }

}

