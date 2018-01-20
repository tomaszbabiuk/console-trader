package org.consoletrader.exchange.bitmarket

import io.reactivex.Single
import org.consoletrader.exchange.CandlesService
import org.consoletrader.common.BaseApi
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTick
import org.ta4j.core.Tick
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class BitmarketCandleService : BaseApi<BitmarketPublicApi>(
        anApi = BitmarketPublicApi::class.java,
        endpoint = "https://www.bitmarket.pl"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Single<MutableList<Tick>> {

        return getApi().queryCandles(pair = "${pair.base}${pair.counter}")
                .toObservable()
                .map { it.reversed() }
                .flatMapIterable { it.reversed() }
                .map {
                    val open = it.open
                    val high = it.high
                    val low = it.low
                    val close = it.close
                    val volume = it.vol
                    val timestamp = it.time

                    val oldJavaDate = Date(timestamp)
                    val instant = oldJavaDate.toInstant()
                    val newJavaDateUtc = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
                    BaseTick(newJavaDateUtc, open, high, low, close, volume) as Tick
                }.toList()

    }

}