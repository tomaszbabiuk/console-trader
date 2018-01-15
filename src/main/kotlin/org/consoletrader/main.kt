package org.consoletrader

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeMatcher
import org.consoletrader.common.Task
import org.consoletrader.notifications.pushover.PushoverNotificationTask
import org.consoletrader.orders.MarketBuyTask
import org.consoletrader.orders.MarketSellTask
import org.consoletrader.rsi.WatchRsiAboveTask
import org.consoletrader.rsi.WatchRsiBelowTask
import org.consoletrader.wallet.WalletTask


fun main(args: Array<String>) {
    val exchangeName = checkArgument(args, "exchange", "Exchange not defined. Allowed markets: binance, bitfinex")
    val apiKey = checkArgument(args, "key", "API key not defined")
    val apiSecret = checkArgument(args, "secret", "Api secret not defined")
    val taskRaw = checkArgument(args, "task", "Task not defined. Allowed tasks: wallet, watchrsibelow, watchrsiabove")
    val actionRaw = checkArgument(args, "action")

    if (args.isEmpty() || exchangeName == null || apiKey == null || apiSecret == null || taskRaw == null) {
        printUsage()
        return
    }

    val exchangeManager = ExchangeMatcher().match(exchangeName, apiKey, apiSecret)
    if (exchangeManager == null) {
        println("The exchange $exchangeName is not supported")
        return
    }

    var actionToExecute = Action {
        println("TASK COMPLETED")
    }

    if (actionRaw != null) {
        val actions = ArrayList<Task>()
        actions += MarketBuyTask(exchangeManager)
        actions += MarketSellTask(exchangeManager)
        actions += PushoverNotificationTask(exchangeManager)
        val matchedAction = actions.firstOrNull { it.match(actionRaw) }
        if (matchedAction != null) {
            actionToExecute = Action {
                matchedAction.execute(actionRaw)
            }
        }
    }

    val tasks = ArrayList<Task>()
    tasks += WalletTask(exchangeManager)
    tasks += MarketBuyTask(exchangeManager)
    tasks += MarketSellTask(exchangeManager)
    tasks += WatchRsiAboveTask(exchangeManager, actionToExecute)
    tasks += WatchRsiBelowTask(exchangeManager, actionToExecute)
    tasks += PushoverNotificationTask(exchangeManager)

    tasks
        .filter { it.match(taskRaw) }
        .forEach { it.execute(taskRaw) }
}

fun checkArgument(args: Array<String>, parameter: String, message: String? = null): String? {
    val paramDefined = args.any { it.startsWith("-$parameter:") }
    if (paramDefined) {
        return args
                .first { it.startsWith("-$parameter:") }
                .substringAfter(':')
    }

    if (message != null) {
        println(message)
    }

    return null
}

fun printUsage() {
    println("""
        USAGE:
        -exchange:[exchange] -key:[key] -secret:[secret] -task:[task] [-action:[action]]

        Parameters:
        [exchange] - exchange name
        [key] - exchange api key
        [secret] - exchange api secret
        [task] - task to do (the list to do)
        [action] - optional, contains additional action to perform when task is completed

        Exchanges:
        -exchange:bitfinex (tested)
        -exchange:binance (testing in process)
        -exchange:bitmarket (in development)

        Tasks:
        -task:wallet - prints list of assets (portfolio/wallet)
        -task:marketbuy([pair]|[value]) - places market buy order on specific pair
        -task:marketsell([pair]|[value]) - places market sell order on specific pair
        -task:watchrsiabove([pair]|[rsi]) - observes RSI on specific pair and completes when the value is above threshold provided
        -task:watchrsibelow([pair]|[rsi]) - observes RSI on specific pair and completes when the value is below threshold provided

        Actions:
        -action:marketbuy([pair]|[value]) - places market buy order on specific pair
        -action:marketsell([pair]|[value]) - places market sell order on specific pair
        -action:pushoveralert([apiKey]|[userId]|[message]) - sends push notification using pushover service

        Examples of tasks/actions (syntax is the same):
        watchrsiabove(XRP/USD|70) - observes RSI of XRP/USD pair and completes when RSI > 70)
        watchrsibelow(XRP/USD|30) - observes RSI of XRP/USD pair and completes when RSI < 30)
        marketbuy(XRP/USD|10XRP) places market order on XRP/USD pair to buy 10XRP
        marketbuy(XRP/USD|10USD) places market order on XRP/USD pair to buy XRP for about 10USD, depending on current market price
    """.trimIndent())


    //TODO:
    //-task:gmailalert(gmailusername|gmailpassword|message)
}