package org.consoletrader.indicators

import org.consoletrader.common.Condition
import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.PairAndDoubleExtendedParams

class ClosePriceAboveConditionFactory(val exchangeManager: ExchangeManager) : PairAndDoubleConditionFactory() {
    override fun create(params: PairAndDoubleExtendedParams): Condition {
        return ClosePriceAboveCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("closepriceabove")
    }
}

class ClosePriceBelowConditionFactory(val exchangeManager: ExchangeManager) : PairAndDoubleConditionFactory() {
    override fun create(params: PairAndDoubleExtendedParams): Condition {
        return ClosePriceBelowCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("closepricebelow")
    }
}