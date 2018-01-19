package org.consoletrader.notifications.pushover

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task
import org.consoletrader.notifications.NotificationExtendedParams

class PushoverNotificationTask(val exchangeManager: ExchangeManager) : Task {

    val sender = PushoverNotificationSender()

    override fun execute(paramsRaw: String) {
        val params = NotificationExtendedParams(paramsRaw)
        sender.sendMessage(params.apiKey, params.userId, params.message)
    }

    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("pushoveralert")
    }
}