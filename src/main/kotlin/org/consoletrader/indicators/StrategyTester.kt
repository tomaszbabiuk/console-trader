package org.consoletrader.indicators

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.ta4j.core.*
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.trading.rules.OverIndicatorRule
import org.ta4j.core.trading.rules.UnderIndicatorRule

//strategy tester is in beta stage... don't use it yet as output is not reliable
class MatchStrategyTask(val exchangeManager: ExchangeManager) : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("matchstrategy")
    }

    override fun execute(paramsRaw: String) {
        val params = PairExtendedParams(paramsRaw)
        println("Calculating best RSI strategy for ${params.currencyPair} pair")
        val dataSource = IndicatorsDataSource(exchangeManager, params.currencyPair)
        dataSource
                .createObservable()
                .map {it.series }
                .subscribe { series ->
            var bestOversold = 0
            var bestOverbought = 0
            var maxGain = 0.0
            val amount = 1000.0
            var bestTradingRecord: TradingRecord? = null
            for (oversold in 10..90) {
                for (overbought in 10..90) {
                    val rsiStrategy = buildRsiStrategy(series, Decimal.valueOf(overbought), Decimal.valueOf(oversold))
                    val seriesManager = TimeSeriesManager(series)
                    val tradingRecord = seriesManager.run(rsiStrategy, Order.OrderType.BUY, Decimal.valueOf(10.0))
                    var totalGain = 0.0
                    tradingRecord.trades.forEach {
                        val entry = it.entry.price.toDouble() * amount
                        val entryCost = entry * 0.002
                        val exit = it.exit.price.toDouble() * amount
                        val exitCost = exit * 0.002
                        val gain = exit - entry - entryCost - exitCost
                        totalGain += gain
                    }

                    if (totalGain > maxGain) {
                        maxGain = totalGain
                        bestOverbought = overbought
                        bestOversold = oversold
                        bestTradingRecord = tradingRecord
                    }
                }
            }

            if (bestTradingRecord != null) {
                println("Max gain: $maxGain, best oversold: $bestOversold, best overbought: $bestOverbought")
                bestTradingRecord.trades.forEach {
                    println("Entry: ${series.getTick(it.entry.index)}")
                    println("Exit: ${series.getTick(it.exit.index)}")
                    println()
                }
            } else {
                println("Best RSI strategy for this pair cannot be calculated. It's better not to invest at this time")
            }
        }
    }

    fun buildRsiStrategy(series: TimeSeries, overbought: Decimal, oversold: Decimal): Strategy {
        val closePrice = ClosePriceIndicator(series)
        val shortSma = SMAIndicator(closePrice, 5)
        val longSma = SMAIndicator(closePrice, 200)

        val rsi = RSIIndicator(closePrice, 14)

        val entryRule = OverIndicatorRule(shortSma, longSma) // Trend
                .and(CrossedDownIndicatorRule(rsi, oversold)) // Signal 1
                .and(OverIndicatorRule(shortSma, closePrice)) // Signal 2

        val exitRule = UnderIndicatorRule(shortSma, longSma) // Trend
                .and(CrossedUpIndicatorRule(rsi, overbought)) // Signal 1
                .and(UnderIndicatorRule(shortSma, closePrice)) // Signal 2


//        val entryRule = OverIndicatorRule(rsi, oversold)
//
//        val exitRule = UnderIndicatorRule(rsi, overbought)

        return BaseStrategy(entryRule, exitRule)
    }
}