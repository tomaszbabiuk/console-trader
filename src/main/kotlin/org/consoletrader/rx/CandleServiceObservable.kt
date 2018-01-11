package org.consoletrader.rx

import io.reactivex.Observable
import org.consoletrader.candles.Candle
import java.util.concurrent.atomic.AtomicReference

object CandleServiceObservable {

    private var candlesReference = AtomicReference<List<Candle>>(ArrayList())

    fun execute(config: CandleServiceConfig): Observable<List<Candle>> {

        val service = config.service
        val pair = config.pair
        val interval = config.interval
        val unit = config.unit
        val candlesCount = config.candlesCount

        return Observable.interval(interval, unit)
                .flatMap { _ -> service.getCandles(pair = pair) }
                .flatMap { candle -> produceCandles(candle, candlesCount) }
                .filter { candles -> candles.size == candlesCount }
    }

    private fun produceCandles(candle: Candle, count: Int): Observable<List<Candle>> {
        var collection = candlesReference.get()

        if (collection.count() < count) {
            collection.plus(candle)
        } else {
            collection.minus(0)
            collection.plus(candle)
        }

        candlesReference.set(collection)

        return Observable.just(collection)
    }

}