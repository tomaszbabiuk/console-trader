package org.consoletrader.roi

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException
import org.knowm.xchange.currency.CurrencyPair

class RoiExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair
    val countedAmount: Double
    val roi: Double

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currencyPair = CurrencyPair(taskParamsSplit[0])
            countedAmount = taskParamsSplit[1].toDouble()
            roi = taskParamsSplit[2].toDouble()
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}