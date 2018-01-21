package org.consoletrader.indicators

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.ExtendedParams
import org.knowm.xchange.currency.CurrencyPair

open class BestOversoldRsiExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair
    val minShortGain: Double
    val minAthLoss: Double
    val rsiAdvance: Double

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currencyPair = CurrencyPair(taskParamsSplit[0])
            rsiAdvance = taskParamsSplit[1].toDouble()
            if (taskParamsSplit.count() == 4) {
                minShortGain = taskParamsSplit[2].toDouble()
                minAthLoss = taskParamsSplit[3].toDouble()
            } else {
                minShortGain = 0.0
                minAthLoss = 0.0
            }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}