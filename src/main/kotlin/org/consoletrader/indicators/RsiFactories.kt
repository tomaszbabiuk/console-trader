package org.consoletrader.indicators

import org.consoletrader.common.Condition
import org.consoletrader.common.ConditionFactory
import org.consoletrader.common.ExchangeManager

abstract class RsiConditionFactory : ConditionFactory {
    override fun create(paramsRaw: String): Condition {
        val params = RsiExtendedParams(paramsRaw)
        return create(params)
    }

    abstract fun create(params: RsiExtendedParams): Condition
}

class RsiAboveConditionFactory(val exchangeManager: ExchangeManager) : RsiConditionFactory() {
    override fun create(params: RsiExtendedParams): Condition {
        return RsiAboveCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("rsiabove")
    }
}

class RsiBelowConditionFactory(val exchangeManager: ExchangeManager) : RsiConditionFactory() {
    override fun create(params: RsiExtendedParams): Condition {
        return RsiBelowCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("rsibelow")
    }
}