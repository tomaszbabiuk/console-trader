package org.consoletrader.notifications.pushover

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException

class PushoverExtendedParams(taskRaw: String) : ExtendedParams() {
    val apiKey: String
    val userId: String
    val message: String

    init {
        try {
            val paramsArray = splitParameters(taskRaw)
            apiKey = paramsArray[0]
            userId = paramsArray[1]
            message = paramsArray[2]
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }
}