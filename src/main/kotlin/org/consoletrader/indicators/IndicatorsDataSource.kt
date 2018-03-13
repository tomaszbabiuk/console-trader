package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.DataSource
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.Tick
import org.ta4j.core.TimeSeries

data class IndicatorsData(val series: TimeSeries, val pair: CurrencyPair)

class IndicatorsDataSource(val exchangeManager: ExchangeManager, val pair: CurrencyPair) : DataSource<IndicatorsData> {
    private val candleService = exchangeManager.candlesService

    override fun create(): Single<IndicatorsData> {
        return candleService
                .getCandles(pair)
                .map {
                    val series = BaseTimeSeries("ta4j_data", it) as TimeSeries
                    IndicatorsData(series, pair)
                }
    }
}