package org.consoletrader.indicators

import io.reactivex.Observable
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.knowm.xchange.currency.CurrencyPair
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

data class AnalyseResult(val pair: CurrencyPair,
                         val minClosePrice: Double,
                         val maxClosePrice: Double,
                         val bestOversoldnRsi: Double,
                         val bestOverboughtRsi: Double) {

    fun calculateGain(): Double {
        return (maxClosePrice / minClosePrice - 1) * 100
    }

    override fun toString(): String {
        return """
            Analyse result of $pair
            Min close price: $minClosePrice
            Max close price: $maxClosePrice
            Max gain: ${calculateGain()}
            Best oversold RSI: $bestOversoldnRsi
            Best overbought RSI: $bestOverboughtRsi
""".trimIndent()
    }
}

class AnalyseTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("analyse")
    }

    override fun execute(paramsRaw: String) {
        val params = PairsExtendedParams(paramsRaw)
        Observable
                .just(params.currencyPairs)
                .flatMapIterable {
                    it
                }
                .flatMap {
                    IndicatorsDataSource(exchangeManager, it).createObservable()
                }
                .map {
                    val series = it.series
                    val closePriceIndicator = ClosePriceIndicator(series)
                    val rsiIndicator = RSIIndicator(closePriceIndicator, 14)
                    var minClosePrice = closePriceIndicator.getValue(0).toDouble()
                    var maxClosePrice = closePriceIndicator.getValue(0).toDouble()
                    var minRsi = rsiIndicator.getValue(series.tickCount - 1).toDouble()
                    var maxRsi = rsiIndicator.getValue(series.tickCount - 1).toDouble()
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

                    AnalyseResult(it.pair, minClosePrice, maxClosePrice, minRsi, maxRsi)
                }
                .subscribe {
                    println(it)
                    println()
                }
    }
}