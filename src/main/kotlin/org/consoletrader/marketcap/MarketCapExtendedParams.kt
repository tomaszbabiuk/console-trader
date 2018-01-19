package org.consoletrader.marketcap

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException

class MarketCapExtendedParams(taskRaw: String) : ExtendedParams() {
    val amount: Double

    init {
        amount = try {
            val paramsArray = splitParameters(taskRaw)
            val amountRaw = paramsArray[0]
            if (amountRaw.toLowerCase().endsWith("bln")) {
                amountRaw.toLowerCase().removeSuffix("bln").toDouble() * 10e8
            } else {
                amountRaw.toDouble()
            }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }
}