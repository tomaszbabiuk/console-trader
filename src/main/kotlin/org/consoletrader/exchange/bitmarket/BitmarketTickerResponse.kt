package org.consoletrader.exchange.bitmarket

data class BitmarketTickerResponse(val time: Long,
                                   val open: Double,
                                   val high: Double,
                                   val low: Double,
                                   val close: Double,
                                   val vol: Double
)