package org.consoletrader.bash

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException

open class ExitExtendedParams(taskRaw: String) : ExtendedParams() {
    val code: Int

    init {
        try {
            val taskParamsSplit = splitParameters(taskRaw)
            code = taskParamsSplit[0].toInt()
            if (code < 2) {
                throw ParsingParameterException("The exit code must be greater than 1 (0 is reserved and it means the task executed successfully, 1 is also reserved and it means that there is some error)!")
            }
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }

}