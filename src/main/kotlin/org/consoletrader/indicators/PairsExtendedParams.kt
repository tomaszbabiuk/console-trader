package org.consoletrader.indicators

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.ExtendedParams
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