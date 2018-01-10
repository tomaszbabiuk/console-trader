package org.consoletrader.domain

import java.util.*

data class Candle (val timestamp: Long = 0,
                   val open: Double = 0.0,
                   val close: Double = 0.0,
                   val high: Double = 0.0,
                   val low: Double = 0.0,
                   var rsi: Double = 0.0,
                   var ema12: Double = 0.0,
                   var ema26: Double = 0.0,
                   var macd: Double = 0.0,
                   var signal: Double = 0.0,
                   var macdHist: Double = 0.0) {
    override fun toString(): String {
        val date = Date(timestamp)
        return "$date [$close], RSI: $rsi, MACD: $macd, Signal: $signal, Hist: $macdHist"
    }

}