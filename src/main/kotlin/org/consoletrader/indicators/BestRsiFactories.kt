package org.consoletrader.indicators

import org.consoletrader.common.Condition
import org.consoletrader.common.ExchangeManager

class BestOversoldRsiConditionFactory(val exchangeManager: ExchangeManager) : PairAndDoubleConditionFactory() {
    override fun create(params: PairAndDoubleExtendedParams): Condition {
        return BestOversoldRsiCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bestoversoldrsi")
    }
}

class BestOverboughtRsiConditionFactory(val exchangeManager: ExchangeManager) : PairAndDoubleConditionFactory() {
    override fun create(params: PairAndDoubleExtendedParams): Condition {
        return BestOverboughtRsiCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("bestoverboughtrsi")
    }
}