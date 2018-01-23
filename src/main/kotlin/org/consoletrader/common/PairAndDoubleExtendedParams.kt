package org.consoletrader.common

import org.knowm.xchange.currency.CurrencyPair

open class PairAndDoubleExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair
    val value: Double

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currencyPair = CurrencyPair(taskParamsSplit[0])
            val valueRaw = taskParamsSplit[1]
            value = valueRaw.toDouble()
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}