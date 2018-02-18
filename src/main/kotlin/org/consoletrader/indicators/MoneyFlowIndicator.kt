package org.consoletrader.indicators

import org.ta4j.core.Decimal
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.RecursiveCachedIndicator
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator

class MoneyFlowIndicator(private val series: TimeSeries, private val timeFrame: Int) : RecursiveCachedIndicator<Decimal>(series) {

    private var typicalPriceIndicator: TypicalPriceIndicator = TypicalPriceIndicator(series)

    override fun calculate(index: Int): Decimal {
        if (index <15) {
            return Decimal.ZERO
        }

        var positiveMoneyFlow = Decimal.ZERO
        var negativeMoneyFlow = Decimal.ZERO

        for (stepBack: Int in 0 until timeFrame) {
            val tick = this.series.getTick(index - stepBack)
            val typicalPrice = typicalPriceIndicator.getValue(index - stepBack)
            val rawMoneyFlow = typicalPrice.multipliedBy(tick.volume)

            if (tick.isBearish) {
                negativeMoneyFlow = negativeMoneyFlow.plus(rawMoneyFlow)
            } else {
                positiveMoneyFlow = positiveMoneyFlow.plus(rawMoneyFlow)
            }
        }
        val moneyFlowRatioCounter = (Decimal.valueOf(timeFrame).minus(positiveMoneyFlow))
        val moneyFlowRatioDenominator = (Decimal.valueOf(timeFrame).minus(negativeMoneyFlow))
        val moneyFlowRatio = moneyFlowRatioCounter.dividedBy(moneyFlowRatioDenominator)

        return Decimal.HUNDRED - Decimal.HUNDRED.dividedBy(Decimal.ONE + moneyFlowRatio)
    }
}