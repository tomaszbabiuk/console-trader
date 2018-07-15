package org.consoletrader.notifications.gmail

import org.consoletrader.common.ExtendedParams
import org.consoletrader.common.ParsingParameterException

class GmailExtendedParams(taskRaw: String) : ExtendedParams() {
    val gmailuser: String
    val gmailpassword: String
    val to: String
    val messageOrFile: String

    init {
        try {
            val paramsArray = splitParameters(taskRaw)
            gmailuser = paramsArray[0]
            gmailpassword = paramsArray[1]
            to = paramsArray[2]
            messageOrFile = paramsArray[3]
        } catch (ex: Exception) {
            throw ParsingParameterException("Error creating task params", ex)
        }
    }
}