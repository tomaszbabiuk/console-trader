package org.consoletrader.notifications.pushover

import org.consoletrader.common.Task

class PushoverNotificationTask() : Task {

    val sender = PushoverNotificationSender()

    override fun execute(paramsRaw: String) {
        val params = PushoverExtendedParams(paramsRaw)
        sender.sendMessage(params.apiKey, params.userId, params.message)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("pushoveralert")
    }
}