package org.consoletrader.orders

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.TaskParams
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.currency.CurrencyPair
import java.math.BigDecimal

open class MarketTaskParams(taskRaw: String) : TaskParams() {
    val currencyPair: CurrencyPair
    val amountValue: BigDecimal
    val amountCurrency: Currency

    init {
        try {
            val taskParamsRaw = taskRaw.substring(taskRaw.indexOf("(") + 1, taskRaw.indexOf(")"))
            val taskParamsSplit = taskParamsRaw.split("|")
            currencyPair = CurrencyPair(taskParamsSplit[0])
            val amountRaw = taskParamsSplit[1]
            val amountInBaseCurrency = amountRaw.endsWith(currencyPair.base.toString(), true)
            val amountInCounterCurrency = amountRaw.endsWith(currencyPair.counter.toString(), true)
            if (!amountInBaseCurrency && !amountInCounterCurrency) {
                val errorMessage = "The amount should be in base or counter currency, eg: 10 ${currencyPair.base} or 15${currencyPair.counter}"
                throw ParsingParameterException(errorMessage)
            }
            amountValue = amountRaw
                    .replace(currencyPair.base.toString(), "")
                    .replace(currencyPair.counter.toString(), "")
                    .toBigDecimal()
            amountCurrency = if (amountInBaseCurrency) {
                currencyPair.base
            } else {
                currencyPair.counter
            }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}