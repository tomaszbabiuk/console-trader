package org.consoletrader.profit

import org.consoletrader.common.Condition
import org.consoletrader.common.ConditionFactory
import org.consoletrader.common.ExchangeManager

class ProfitConditionFactory(val exchangeManager: ExchangeManager) : ConditionFactory {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("minprofit")
    }

    override fun create(paramsRaw: String): Condition {
        val params = ProfitExtendedParams(paramsRaw)
        return ProfitCondition(exchangeManager, params)
    }
}