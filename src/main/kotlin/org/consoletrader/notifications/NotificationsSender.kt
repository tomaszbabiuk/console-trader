package org.consoletrader.notifications

interface NotificationsSender {

    fun sendMessage(apiKey: String, userId: String, message: String)
}