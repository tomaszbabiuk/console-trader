package org.consoletrader

import org.consoletrader.tasks.ListAssetsTask


fun main(args: Array<String>) {
    val exchangeName = checkArgument(args, "market", "Market not defined. Allowed markets: binance")
    val apiKey = checkArgument(args, "key", "API key not defined")
    val apiSecret = checkArgument(args, "secret", "Api secret not defined")
    val task = checkArgument(args, "task", "Task not defined. Allowed tasks: cancelorders, listorders, buy")

    if (args.isEmpty() || exchangeName == null || apiKey == null || apiSecret == null || task == null) {
        printUsage()
        return
    }

    val exchange = ExchangeMatcher().match(exchangeName, apiKey, apiSecret)
    if (exchange == null) {
        println("The exchange $exchangeName is not supported")
        return
    }

    when (task) {
        "wallet" -> ListAssetsTask(exchange).execute()
        else -> {
            println("Unknown task!")
        }
    }
}

fun checkArgument(args: Array<String>, parameter: String, message: String): String? {
    val paramDefined = args.any { it.startsWith("-$parameter:") }
    if (paramDefined) {
        return args
                .first { it.startsWith("-$parameter:") }
                .substringAfter(':')
    }

    println(message)
    return null
}

fun printUsage() {
    println("USAGE:")
    println("-exchange:[exchange] -key:[key] -secret:[secret] -task:wallet")
}