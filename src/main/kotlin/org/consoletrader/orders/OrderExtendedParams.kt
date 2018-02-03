package org.consoletrader.orders

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.ExtendedParams
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.currency.CurrencyPair
import java.math.BigDecimal

open class OrderExtendedParams(taskRaw: String) : ExtendedParams() {
    val currencyPair: CurrencyPair
    val amountValue: BigDecimal
    val amountCurrency: Currency
    var price: Double = 0.0

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

            if (taskParamsSplit.size == 3) {
                price = taskParamsSplit[2].toDouble()
            }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}