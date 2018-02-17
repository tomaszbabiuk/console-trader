package org.consoletrader.common

import org.consoletrader.indicators.IndicatorsData
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.SMAIndicator


class AnalyseResult(indicatorsData: IndicatorsData) {
    val currentPrice: Double
    val pair: CurrencyPair
    var minClosePrice: Double
    var maxClosePrice: Double
    var currentRsi: Double
    val bestOversoldRsi: Double
    val bestOverboughtRsi: Double
    val ema14: Double
    val standardDeviation: Double
    val lowBollingerBand: Double
    val upBollingerBand: Double

    init {
        val series = indicatorsData.series
        val closePriceIndicator = ClosePriceIndicator(series)
        val rsiIndicator = RSIIndicator(closePriceIndicator, 14)

        val ema20Indicator = EMAIndicator(closePriceIndicator, 20)
        val sd20Indicator = StandardDeviationIndicator(closePriceIndicator, 20)

        val middleBand = BollingerBandsMiddleIndicator(ema20Indicator)
        val lowBand = BollingerBandsLowerIndicator(middleBand, sd20Indicator)
        val upBand = BollingerBandsUpperIndicator(middleBand, sd20Indicator)

        pair = indicatorsData.pair
        currentPrice = closePriceIndicator.getValue(series.tickCount - 1).toDouble()
        minClosePrice = closePriceIndicator.getValue(0).toDouble()
        maxClosePrice = closePriceIndicator.getValue(0).toDouble()
        currentRsi = rsiIndicator.getValue(series.tickCount - 1).toDouble()
        ema14 = ema20Indicator.getValue(series.tickCount - 1).toDouble()
        standardDeviation = sd20Indicator.getValue(series.tickCount - 1).toDouble()
        lowBollingerBand = lowBand.getValue(series.tickCount - 1).toDouble()
        upBollingerBand = upBand.getValue(series.tickCount - 1).toDouble()


        var minRsi = currentRsi
        var maxRsi = currentRsi
        for (i in 14 until series.tickCount) {
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
            Up Bollinger band: $upBollingerBand
            EMA 14: $ema14
            Low Bollinger band: $lowBollingerBand
            Short gain: ${calculateGain()}
            Short ATH loss: ${calculateAthLoss()}
""".trimIndent()
    }
}