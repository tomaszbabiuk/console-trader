package org.consoletrader.notifications.pushover

data class PushoverRequestParams(val token: String, val user: String, val message: String, val priority: Int = 1)