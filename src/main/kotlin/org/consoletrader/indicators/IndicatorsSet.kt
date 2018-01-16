package org.consoletrader.indicators

import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.*
import org.ta4j.core.indicators.helpers.ClosePriceIndicator


class IndicatorsSet(val series: TimeSeries) {
    val closePriceIndicator: ClosePriceIndicator
    val shortEMAIndicator: EMAIndicator
    val longEMAIndicator: EMAIndicator
    val stochasticOscillatorKIndicator: StochasticOscillatorKIndicator
    val rsiIndicator: RSIIndicator
    var macdIndicator: MACDIndicator
    var emaMacdIndicator: EMAIndicator

    init {
        closePriceIndicator = ClosePriceIndicator(series)
        shortEMAIndicator = EMAIndicator(closePriceIndicator, 9)
        longEMAIndicator = EMAIndicator(closePriceIndicator, 26)
        stochasticOscillatorKIndicator = StochasticOscillatorKIndicator(series, 14)
        macdIndicator = MACDIndicator(closePriceIndicator, 9, 26)
        emaMacdIndicator = EMAIndicator(macdIndicator, 18)
        rsiIndicator = RSIIndicator(closePriceIndicator, 14)
    }

    override fun toString(): String {
        val closePrice = closePriceIndicator.getValue(series.tickCount - 1)
        val rsi = rsiIndicator.getValue(series.tickCount - 1)
        val shortEma = shortEMAIndicator.getValue(series.tickCount - 1)
        val longEma = longEMAIndicator.getValue(series.tickCount - 1)
        val stochastic = stochasticOscillatorKIndicator.getValue(series.tickCount - 1)
        val macd = macdIndicator.getValue(series.tickCount - 1)
        val macdEma = emaMacdIndicator.getValue(series.tickCount - 1)
        return """
            Close price: $closePrice
                    RSI: $rsi
              Short EMA: $shortEma
               Long EMA: $longEma
  Stochastic oscillator: $stochastic
                   MACD: $macd
               MACD EMA: $macdEma
               """.trimIndent()
    }
}

