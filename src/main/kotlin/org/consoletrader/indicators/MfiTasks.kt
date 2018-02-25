package org.consoletrader.indicators

import io.reactivex.Single
import org.consoletrader.common.*
import org.ta4j.core.TimeSeries

abstract class MfiTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, PairAndDoubleExtendedParams>() {
    override fun createParams(paramsRaw: String): PairAndDoubleExtendedParams {
        return PairAndDoubleExtendedParams(paramsRaw)
    }

    override fun createDataSource(params: PairAndDoubleExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }
    }
}

class MfiAboveTask(exchangeManager: ExchangeManager) : MfiTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val mfiIndicator = MoneyFlowIndicator(data, 14)
        val mfi = mfiIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = mfi > params.value
        println("[${passed.toString().toUpperCase()}] MFI of ${params.currencyPair}: $mfi > ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("mfiabove")
    }
}

class MfiBelowTask(exchangeManager: ExchangeManager) : MfiTask(exchangeManager) {
    override fun verifySuccess(data: TimeSeries, params: PairAndDoubleExtendedParams): Boolean {
        val mfiIndicator = MoneyFlowIndicator(data, 14)
        val mfi = mfiIndicator.getValue(data.tickCount - 1).toDouble()
        val passed = mfi < params.value
        println("[${passed.toString().toUpperCase()}] MFI of ${params.currencyPair}: $mfi < ${params.value}")
        return passed
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("mfibelow")
    }
}

class MfiPumpTask(val exchangeManager: ExchangeManager) : DataSourceTask<TimeSeries, VPatternOfMfiExtendedParams>() {

    override fun createParams(paramsRaw: String): VPatternOfMfiExtendedParams {
        return VPatternOfMfiExtendedParams(paramsRaw)
    }

    override fun createDataSource(params: VPatternOfMfiExtendedParams): Single<TimeSeries> {
        return IndicatorsDataSource(exchangeManager, params.currencyPair)
                .create()
                .map { it.series }
    }

    override fun verifySuccess(data: TimeSeries, params: VPatternOfMfiExtendedParams): Boolean {
        val mfiIndicator = MoneyFlowIndicator(data, params.mfiLength)

        println("DEBUG - Money Flow Index values:")
        for (index in 15..data.tickCount -1) {
            val tick = data.getTick(index)
            val mfi = mfiIndicator.getValue(index).toDouble()
            println("${tick.endTime}: $mfi")
        }
        println()

        val mfiThirdTopPeakN = mfiIndicator.getValue(data.tickCount - 1).toDouble()
        val mfiThirdTopPeakNMinus1 = mfiIndicator.getValue(data.tickCount - 2).toDouble()
        val mfiThirdTopPeakNMinus2 = mfiIndicator.getValue(data.tickCount - 3).toDouble()

        val secondTopPeakFound = mfiThirdTopPeakNMinus1 > mfiThirdTopPeakNMinus2 && mfiThirdTopPeakNMinus1 > mfiThirdTopPeakN
        if (secondTopPeakFound) {
            var minMfi = mfiThirdTopPeakNMinus1
            var minMfiIndex = data.tickCount - 2
            for (index in 2..params.timeFrames) {
                val iMfi = mfiIndicator.getValue(data.tickCount -index).toDouble()
                if (iMfi<minMfi) {
                    minMfi = iMfi
                    minMfiIndex = data.tickCount - index
                }
            }

            val mfiSecondBottomPeakN = mfiIndicator.getValue(minMfiIndex).toDouble()
            val mfiSecondBottomPeakNMinus1 = mfiIndicator.getValue(minMfiIndex -1).toDouble()
            val mfiSecondDownPeakNPlus1 = mfiIndicator.getValue(minMfiIndex + 1).toDouble()

            val bottomPeakFound = mfiSecondBottomPeakN < mfiSecondBottomPeakNMinus1 && mfiSecondBottomPeakN < mfiSecondDownPeakNPlus1
            if (bottomPeakFound) {
                var mfiFirstUpPeak = mfiSecondBottomPeakN
                for (index in minMfiIndex downTo minMfiIndex - params.timeFrames) {
                    val iMfi = mfiIndicator.getValue(index).toDouble()
                    if (iMfi > mfiFirstUpPeak) {
                        mfiFirstUpPeak = iMfi
                    }
                }

                val firstPeakMatchesVPattern = mfiFirstUpPeak > params.firstTopPeakAbove
                val secondPeakMatchesVPattern = mfiSecondBottomPeakN < params.secondBottomPeakBelow
                val thirdPeakMatchesVPattern = mfiThirdTopPeakNMinus1 > params.thirdTopPeakAbove

                println("[${firstPeakMatchesVPattern.toString().toUpperCase()}] First up peak above: $mfiFirstUpPeak > ${params.firstTopPeakAbove}")
                println("[${secondPeakMatchesVPattern.toString().toUpperCase()}] Second bottom peak below: $mfiSecondBottomPeakN < ${params.secondBottomPeakBelow}")
                println("[${thirdPeakMatchesVPattern.toString().toUpperCase()}] Third up peak above: $mfiThirdTopPeakNMinus1 > ${params.thirdTopPeakAbove}")

                return firstPeakMatchesVPattern && secondPeakMatchesVPattern && thirdPeakMatchesVPattern
            } else {
                println("[FALSE] Pattern didn't match: only third up peak found")
                return false
            }
        } else {
            println("[FALSE] No peaks matching V pattern found")
            return false
        }
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("mfivpattern")
    }
}

