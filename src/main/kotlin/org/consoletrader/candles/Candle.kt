package org.consoletrader.candles

import java.util.*

data class Candle (val timestamp: Long = 0,
                   val open: Double = 0.0,
                   val close: Double = 0.0,
                   val high: Double = 0.0,
                   val low: Double = 0.0,
                   var volume: Double = 0.0) {
    override fun toString(): String {
        val date = Date(timestamp)
        return "$date open=$open, close=$close, high=$high, low=$low"
    }

}