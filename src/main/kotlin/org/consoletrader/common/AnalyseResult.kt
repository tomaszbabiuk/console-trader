package org.consoletrader.common

import org.consoletrader.indicators.IndicatorsData
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator


class AnalyseResult(indicatorsData: IndicatorsData) {
    val currentPrice: Double
    val pair: CurrencyPair
    var minClosePrice: Double
    var maxClosePrice: Double
    var currentRsi: Double
    val bestOversoldRsi: Double
    val bestOverboughtRsi: Double

    init {
        val series = indicatorsData.series
        val closePriceIndicator = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePriceIndicator, 14)
        pair = indicatorsData.pair
        currentPrice = closePriceIndicator.getValue(series.tickCount - 1).toDouble()
        minClosePrice = closePriceIndicator.getValue(0).toDouble()
        maxClosePrice = closePriceIndicator.getValue(0).toDouble()
        currentRsi = rsiIndicator.getValue(series.tickCount - 1).toDouble()
        var minRsi = currentRsi
        var maxRsi = currentRsi
        for (i in 0 until series.tickCount) {
            val xPrice = closePriceIndicator.getValue(i).toDouble()
            if (xPrice < minClosePrice) {
                minClosePrice = xPrice
            }
            if (xPrice > maxClosePrice) {
                maxClosePrice = xPrice
            }

            val rsi = rsiIndicator.getValue(i).toDouble()
            if (i > 14) {
                if (rsi < minRsi) {
                    minRsi = rsi
                }
                if (rsi > maxRsi) {
                    maxRsi = rsi
                }
            }
        }

        bestOversoldRsi = minRsi
        bestOverboughtRsi = maxRsi
    }

    fun calculateGain(): Double {
        return (maxClosePrice / minClosePrice - 1) * 100
    }

    fun calculateAthLoss(): Double {
        return (1 - currentPrice / maxClosePrice) * 100
    }

    override fun toString(): String {
        return """
            Analyse result of $pair
            Current price: $currentPrice
            Min close price: $minClosePrice
            Max close price: $maxClosePrice
            Current RSI: $currentRsi
            Best oversold RSI: $bestOversoldRsi
            Best overbought RSI: $bestOverboughtRsi
            Max gain: ${calculateGain()}
            Short ATH loss: ${calculateAthLoss()}
""".trimIndent()
    }
}