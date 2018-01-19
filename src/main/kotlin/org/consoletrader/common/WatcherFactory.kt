package org.consoletrader.common

interface WatcherFactory : Matcher {
    fun create(paramsRaw: String): Condition
}