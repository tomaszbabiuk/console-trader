package org.consoletrader.indicators

import io.reactivex.Observable
import io.reactivex.Single
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.DataSource
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.TimeSeries


class IndicatorsDataSource(exchangeManager: ExchangeManager, private val pair: CurrencyPair) : DataSource<TimeSeries> {
    private val candleService = exchangeManager.candlesService

    override fun createSingle(): Single<TimeSeries> {
        return candleService
                .getCandles(pair)
                .map {
                    BaseTimeSeries("ta4j_data", it) as TimeSeries
                }
    }

    override fun createObservable(): Observable<TimeSeries> {
        return createSingle()
                .toObservable()
    }
}