package org.consoletrader.indicators

import org.consoletrader.common.Condition
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams

class RsiAboveConditionFactory(val exchangeManager: ExchangeManager) : PairAndDoubleConditionFactory() {
    override fun create(params: PairAndDoubleExtendedParams): Condition {
        return RsiAboveCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("rsiabove")
    }
}

class RsiBelowConditionFactory(val exchangeManager: ExchangeManager) : PairAndDoubleConditionFactory() {
    override fun create(params: PairAndDoubleExtendedParams): Condition {
        return RsiBelowCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("rsibelow")
    }
}