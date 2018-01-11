package org.consoletrader.rx

import org.consoletrader.candles.CandlesService
import org.knowm.xchange.currency.CurrencyPair
import java.util.concurrent.TimeUnit

data class CandleServiceConfig(val service: CandlesService,
                               val pair: CurrencyPair,
                               val interval: Long,
                               val unit: TimeUnit,
                               val candlesCount: Int)