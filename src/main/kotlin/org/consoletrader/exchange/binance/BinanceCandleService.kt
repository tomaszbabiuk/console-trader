package org.consoletrader.exchange.binance

import io.reactivex.Single
import org.consoletrader.exchange.CandlesService
import org.consoletrader.common.BaseApi
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTick
import org.ta4j.core.Tick
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class BinanceCandleService : BaseApi<BinancePublicAPI>(
        anApi = BinancePublicAPI::class.java,
        endpoint = "https://api.binance.com"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Single<MutableList<Tick>> {
        return getApi()
                .queryCandles("${pair.base}${pair.counter}")
                .map { it.reversed() }
                .flatMapIterable { it.reversed() }
                .map {
                    val open = it[1].toDouble()
                    val high = it[2].toDouble()
                    val low = it[3].toDouble()
                    val close = it[3].toDouble()
                    val volume = it[5].toDouble()
                    val timestamp = it[6].toLong()

                    val oldJavaDate = Date(timestamp)
                    val instant = oldJavaDate.toInstant()
                    val newJavaDateUtc = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
                    BaseTick(newJavaDateUtc, open, high, low, close, volume) as Tick
                }
                .toList()
    }
}

