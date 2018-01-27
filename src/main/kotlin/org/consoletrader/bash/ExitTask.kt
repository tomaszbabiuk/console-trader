package org.consoletrader.bash

import org.consoletrader.common.Task
import kotlin.system.exitProcess

class ExitTask : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("exit")
    }

    override fun execute(paramsRaw: String) {
        val params = ExitExtendedParams(paramsRaw)
        exitProcess(params.code)
    }
}