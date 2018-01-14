package org.consoletrader.common

abstract class Task(val exchangeManager: ExchangeManager) {
    abstract fun execute(taskRaw: String)
    abstract fun match(taskRaw: String): Boolean
}