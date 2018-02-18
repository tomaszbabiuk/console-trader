package org.consoletrader.indicators

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.ExtendedParams
import org.knowm.xchange.currency.CurrencyPair

open class VPatternOfMfiExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair
    val firstTopPeakAbove: Double
    val secondBottomPeakBelow: Double
    val thirdTopPeakAbove: Double
    val timeFrame: Int

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currencyPair = CurrencyPair(taskParamsSplit[0])
            firstTopPeakAbove = taskParamsSplit[1].toDouble()
            secondBottomPeakBelow = taskParamsSplit[2].toDouble()
            thirdTopPeakAbove = taskParamsSplit[3].toDouble()
            timeFrame = if (taskParamsSplit.count() == 5) {
                            taskParamsSplit[4].toInt()
                        } else {
                            14
                        }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}