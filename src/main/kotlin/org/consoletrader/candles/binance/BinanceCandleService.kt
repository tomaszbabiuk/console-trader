package org.consoletrader.candles.binance

import io.reactivex.Observable
import okhttp3.OkHttpClient
import org.consoletrader.candles.Candle
import org.consoletrader.candles.CandlesService
import org.knowm.xchange.currency.CurrencyPair
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BinanceCandleService : CandlesService {
    private var api: BinancePublicAPI

    init {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val builder = Retrofit.Builder()
                .baseUrl("https://api.binance.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        api = builder.create(BinancePublicAPI::class.java)
    }

    override fun getCandles(pair: CurrencyPair): Observable<Candle> {
        return api
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

