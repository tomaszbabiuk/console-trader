package org.consoletrader.wallet

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException
import org.knowm.xchange.currency.Currency

open class CurrencyAndValueExtendedParams(taskRaw: String) : ExtendedParams() {
    val currency: Currency
    val value: Double

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currency = Currency(taskParamsSplit[0])
            value = taskParamsSplit[1].toDouble()
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}