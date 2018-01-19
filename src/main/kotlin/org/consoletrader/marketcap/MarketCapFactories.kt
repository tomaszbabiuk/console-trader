package org.consoletrader.marketcap

import org.consoletrader.common.Condition
import org.consoletrader.common.ConditionFactory

abstract class MarketCapConditionFactory : ConditionFactory {
    override fun create(paramsRaw: String): Condition {
        val marketCapExtendedParams = MarketCapExtendedParams(paramsRaw)
        return create(marketCapExtendedParams)
    }

    abstract fun create(marketCap: MarketCapExtendedParams): Condition
}

class MarketCapAboveConditionFactory : MarketCapConditionFactory() {
    override fun create(marketCap: MarketCapExtendedParams): Condition {
        return MarketCapAboveCondition(marketCap)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapabove")
    }
}

class MarketCapBelowConditionFactory : MarketCapConditionFactory() {
    override fun create(marketCap: MarketCapExtendedParams): Condition {
        return MarketCapBelowCondition(marketCap)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapbelow")
    }
}