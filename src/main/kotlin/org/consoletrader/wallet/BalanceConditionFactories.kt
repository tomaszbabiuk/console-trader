package org.consoletrader.wallet

import org.consoletrader.common.Condition
import org.consoletrader.common.ConditionFactory
import org.consoletrader.common.ExchangeManager

class BalanceAboveConditionFactory(val exchangeManager: ExchangeManager) : ConditionFactory {
    override fun create(paramsRaw: String): Condition {
        val params = CurrencyAndValueExtendedParams(paramsRaw)
        return BalanceAboveCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("balanceabove")
    }
}

class BalanceBelowConditionFactory(val exchangeManager: ExchangeManager) : ConditionFactory {
    override fun create(paramsRaw: String): Condition {
        val params = CurrencyAndValueExtendedParams(paramsRaw)
        return MaxBalanceCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("balancebelow")
    }
}