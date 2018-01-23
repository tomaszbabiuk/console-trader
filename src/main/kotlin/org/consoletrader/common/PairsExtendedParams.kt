package org.consoletrader.common

import org.knowm.xchange.currency.CurrencyPair

open class PairsExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPairs: Iterable<CurrencyPair>

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            val currencyPairsRaw = taskParamsSplit[0]
            currencyPairs = currencyPairsRaw
                    .split(',')
                    .map { CurrencyPair(it) }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}