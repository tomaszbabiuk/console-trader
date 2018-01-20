package org.consoletrader.indicators

import org.consoletrader.common.Condition
import org.consoletrader.common.ConditionFactory
import org.consoletrader.common.ExchangeManager

abstract class MacdConditionFactory : ConditionFactory {
    override fun create(paramsRaw: String): Condition {
        val params = PairExtendedParams(paramsRaw)
        return create(params)
    }

    abstract fun create(params: PairExtendedParams): Condition
}

class MacdCrossUpConditionFactory(val exchangeManager: ExchangeManager) : MacdConditionFactory() {
    override fun create(params: PairExtendedParams): Condition {
        return MacdCrossUpCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("macdcrossup")
    }
}

class MacdCrossDownConditionFactory(val exchangeManager: ExchangeManager) : MacdConditionFactory() {
    override fun create(params: PairExtendedParams): Condition {
        return MacdCrossDownCondition(exchangeManager, params)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("macdcrossdown")
    }
}