package org.consoletrader.common

interface ConditionFactory : Matcher {
    fun create(paramsRaw: String): Condition
}