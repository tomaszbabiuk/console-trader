package org.consoletrader.candles.bitmarket

data class BitmarketTickerResponse(val ask: Double,
                                   val bid: Double,
                                   val last: Double,
                                   val low: Double,
                                   val high: Double,
                                   val vwap: Double,
                                   val volume: Double
)