package org.consoletrader.indicators

import org.consoletrader.common.Condition
import org.consoletrader.common.ConditionFactory
import org.consoletrader.common.PairAndDoubleExtendedParams

abstract class PairAndDoubleConditionFactory : ConditionFactory {
    override fun create(paramsRaw: String): Condition {
        val params = PairAndDoubleExtendedParams(paramsRaw)
        return create(params)
    }

    abstract fun create(params: PairAndDoubleExtendedParams): Condition
}