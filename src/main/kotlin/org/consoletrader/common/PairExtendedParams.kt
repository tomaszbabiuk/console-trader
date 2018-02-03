package org.consoletrader.common

import org.knowm.xchange.currency.CurrencyPair

open class PairExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            currencyPair = CurrencyPair(taskParamsSplit[0])
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}