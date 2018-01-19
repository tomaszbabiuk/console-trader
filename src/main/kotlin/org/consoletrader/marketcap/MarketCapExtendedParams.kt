package org.consoletrader.marketcap

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException

class MarketCapExtendedParams(taskRaw: String) : ExtendedParams() {
    val amount: Double

    init {
        try {
            val paramsArray = splitParameters(taskRaw)
            amount = paramsArray[0].toDouble()
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }
}