package org.consoletrader.candles.bitfinex

import io.reactivex.Observable
import okhttp3.OkHttpClient
import org.consoletrader.candles.Candle
import org.consoletrader.candles.CandlesService
import org.knowm.xchange.currency.CurrencyPair
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BitfinexCandleService : CandlesService {
    private var api: BitfinexPublicAPI

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val builder = Retrofit.Builder()
                .baseUrl("https://api.bitfinex.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        api = builder.create(BitfinexPublicAPI::class.java)
    }

    override fun getCandles(pair: CurrencyPair): Observable<Candle> {
        return api
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

