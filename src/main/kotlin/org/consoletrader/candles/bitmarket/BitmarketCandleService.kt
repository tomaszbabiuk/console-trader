package org.consoletrader.candles.bitmarket

import io.reactivex.Observable
import org.consoletrader.candles.Candle
import org.consoletrader.candles.CandlesService
import org.consoletrader.candles.base.BaseApi
import org.knowm.xchange.currency.CurrencyPair
import java.time.Instant

class BitmarketCandleService : BaseApi<BitmarketPublicApi>(anApi = BitmarketPublicApi::class.java, endpoint = "https://www.bitmarket.pl/"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Observable<Candle> {

        //todo: pair is correct???
        return getApi().query(pair = pair.toString())
                .map {
                    val timestamp = Instant.EPOCH.epochSecond
                    //todo: review if this data is correct!!!
                    Candle(timestamp = timestamp,
                            open = it.ask,
                            close = it.bid,
                            high = it.high,
                            low = it.low,
                            volume = it.volume)
                }
    }

}