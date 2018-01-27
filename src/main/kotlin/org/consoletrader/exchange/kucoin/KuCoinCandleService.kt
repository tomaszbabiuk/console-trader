package org.consoletrader.exchange.kucoin

import io.reactivex.Single
import org.consoletrader.exchange.CandlesService
import org.consoletrader.common.BaseApi
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTick
import org.ta4j.core.Tick
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

class KuCoinCandleService : BaseApi<KuCoinPublicApi>(
        anApi = KuCoinPublicApi::class.java,
        endpoint = "https://api.kucoin.com"),
        CandlesService {

    override fun getCandles(pair: CurrencyPair): Single<MutableList<Tick>> {
        val now = Calendar.getInstance().timeInMillis/1000
        val from = now - 1800 * 600 // = 600 clines of 30(1800 secs) resolution

        return getApi().queryCandles("${pair.base}-${pair.counter}", from, now)
                .flatMap {
                    val result = ArrayList<Tick>()
                    it.c.forEachIndexed { index, close ->
                        val high = it.h[index]
                        val low = it.l[index]
                        val open = it.o[index]
                        val volume = it.v[index]
                        val timestamp = it.t[index]

                        val oldJavaDate = Date(timestamp * 1000)
                        val instant = oldJavaDate.toInstant()
                        val newJavaDateUtc = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
                        val tick = BaseTick(newJavaDateUtc, open, high, low, close, volume)
                        result.add(tick)
                    }
                    Single.just(result)
                }
    }

}