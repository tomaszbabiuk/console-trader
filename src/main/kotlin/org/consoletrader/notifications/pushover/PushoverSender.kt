package org.consoletrader.notifications.pushover

interface PushoverSender {

    fun sendMessage(apiKey: String, userId: String, message: String)
}