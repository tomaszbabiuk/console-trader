package org.consoletrader.common

import org.knowm.xchange.currency.Currency

open class CurrencyExtendedParams(taskRaw: String) : ExtendedParams() {
    val currency: Currency

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currency = Currency(taskParamsSplit[0])
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}