package org.consoletrader.orders

import org.consoletrader.common.ParsingParameterException
import org.consoletrader.common.ExtendedParams
import org.knowm.xchange.currency.CurrencyPair
import java.math.BigDecimal

open class OrderExtendedParams(taskRaw: String) : ExtendedParams() {
    enum class AmountUnit {
        BaseCurrency, CounterCurrency, Percent
    }

    val currencyPair: CurrencyPair
    val amountValue: BigDecimal
    var price: Double = 0.0
    var amountUnit: AmountUnit

    init {
        try {
            val taskParamsRaw = taskRaw.substring(taskRaw.indexOf("(") + 1, taskRaw.indexOf(")"))
            val taskParamsSplit = taskParamsRaw.split("|")
            currencyPair = CurrencyPair(taskParamsSplit[0])
            val amountRaw = taskParamsSplit[1]
            val amountInBaseCurrency = amountRaw.endsWith(currencyPair.base.toString(), true)
            val amountInCounterCurrency = amountRaw.endsWith(currencyPair.counter.toString(), true)
            val amountInPercent = amountRaw.endsWith("%", true)
            amountUnit = if (amountInBaseCurrency) {
                AmountUnit.BaseCurrency
            } else if (amountInCounterCurrency) {
                AmountUnit.CounterCurrency
            } else {
                AmountUnit.Percent
            }

            if (!amountInBaseCurrency && !amountInCounterCurrency && !amountInPercent) {
                val errorMessage = "The amount should be in base currency, counter currency, or percent eg: 10 ${currencyPair.base}, 15${currencyPair.counter} or 15%"
                throw ParsingParameterException(errorMessage)
            }
            amountValue = amountRaw
                    .replace(currencyPair.base.toString(), "")
                    .replace(currencyPair.counter.toString(), "")
                    .replace("%","")
                    .toBigDecimal()

            if (taskParamsSplit.size == 3) {
                price = taskParamsSplit[2].toDouble()
            }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}