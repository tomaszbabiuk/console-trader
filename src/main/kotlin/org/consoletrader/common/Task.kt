package org.consoletrader.common

interface Task {
    fun match(paramsRaw: String): Boolean
    fun execute(paramsRaw: String)
}