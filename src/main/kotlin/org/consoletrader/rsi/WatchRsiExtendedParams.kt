package org.consoletrader.rsi

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.ExtendedParams
import org.knowm.xchange.currency.CurrencyPair

open class WatchRsiExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair
    val rsi: Double

    init {
        try {
            val taskParamsRaw = taskRaw.substring(taskRaw.indexOf("(") + 1, taskRaw.indexOf(")"))
            val taskParamsSplit = taskParamsRaw.split("|")
            currencyPair = CurrencyPair(taskParamsSplit[0])
            val rsiRaw = taskParamsSplit[1]
            rsi = rsiRaw.toDouble()
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}