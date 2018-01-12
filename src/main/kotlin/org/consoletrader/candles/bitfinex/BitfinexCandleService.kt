package org.consoletrader.candles.bitfinex

import io.reactivex.Observable
import org.consoletrader.candles.Candle
import io.reactivex.Single
import okhttp3.OkHttpClient
import org.consoletrader.candles.CandlesService
import org.consoletrader.candles.base.BaseApi
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTick
import org.ta4j.core.Tick
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class BitfinexCandleService : BaseApi<BitfinexPublicAPI>(
        anApi = BitfinexPublicAPI::class.java,
        endpoint = "https://api.bitfinex.com"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Single<MutableList<Tick>> {
        return getApi()
                .queryCandles("${pair.base}${pair.counter}")
                .flatMapIterable { it.reversed() }
                .map {
                    val timestamp = it[0].toLong()
                    val open = it[1]
                    val close = it[2]
                    val high = it[3]
                    val low = it[4]
                    val volume = it[5]

                    val oldJavaDate = Date(timestamp)
                    val instant = oldJavaDate.toInstant()
                    val newJavaDateUtc = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
                    BaseTick(newJavaDateUtc, open, high, low, close, volume) as Tick
                }
                .toList()

    }
}


