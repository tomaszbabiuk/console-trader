package org.consoletrader.common

abstract class Task(val exchangeManager: ExchangeManager) {
    abstract fun execute(paramsRaw: String)
    abstract fun match(paramsRaw: String): Boolean
}