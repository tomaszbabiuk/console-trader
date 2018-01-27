package org.consoletrader

import io.reactivex.functions.Action
import org.consoletrader.common.*
import org.consoletrader.exchange.ExchangeMatcher
import org.consoletrader.indicators.*
import org.consoletrader.marketcap.*
import org.consoletrader.notifications.pushover.PushoverNotificationTask
import org.consoletrader.orders.MarketBuyTask
import org.consoletrader.orders.MarketSellTask
import org.consoletrader.profit.CalculateStopPriceTask
import org.consoletrader.profit.ProfitConditionFactory
import org.consoletrader.strategy.BuyingStrategyTask
import org.consoletrader.strategy.MatchStrategyTask
import org.consoletrader.strategy.SellingStrategyTask
import org.consoletrader.wallet.WalletTask


fun main(args: Array<String>) {
    val exchangeName = checkArgument(args, "exchange", "Exchange not defined. Allowed markets: binance, bitfinex")
    val apiKey = checkArgument(args, "key", "API key not defined")
    val apiSecret = checkArgument(args, "secret", "Api secret not defined")
    val taskRaw = checkArgument(args, "task", "Task not defined. Allowed tasks: wallet, watchrsibelow, watchrsiabove")

    if (args.isEmpty() || exchangeName == null || apiKey == null || apiSecret == null || taskRaw == null) {
        printUsage()
        return
    }

    val exchangeManager = ExchangeMatcher().match(exchangeName, apiKey, apiSecret)
    if (exchangeManager == null) {
        println("The exchange $exchangeName is not supported")
        return
    }

    val allConditionFactories = ArrayList<ConditionFactory>()
    allConditionFactories.add(MarketCapAboveConditionFactory())
    allConditionFactories.add(MarketCapBelowConditionFactory())
    allConditionFactories.add(RsiAboveConditionFactory(exchangeManager))
    allConditionFactories.add(RsiBelowConditionFactory(exchangeManager))
    allConditionFactories.add(MacdCrossUpConditionFactory(exchangeManager))
    allConditionFactories.add(MacdCrossDownConditionFactory(exchangeManager))
    allConditionFactories.add(ClosePriceAboveConditionFactory(exchangeManager))
    allConditionFactories.add(ClosePriceBelowConditionFactory(exchangeManager))
    allConditionFactories.add(BestOversoldRsiConditionFactory(exchangeManager))
    allConditionFactories.add(BestOverboughtRsiConditionFactory(exchangeManager))
    allConditionFactories.add(ProfitConditionFactory(exchangeManager))

    val allTasks = ArrayList<Task>()
    allTasks += WalletTask(exchangeManager)
    allTasks += MarketBuyTask(exchangeManager)
    allTasks += MarketSellTask(exchangeManager)
    allTasks += MatchStrategyTask(exchangeManager)
    allTasks += PushoverNotificationTask(exchangeManager)
    allTasks += BuyingStrategyTask(exchangeManager)
    allTasks += SellingStrategyTask(exchangeManager)
    allTasks += CalculateStopPriceTask(exchangeManager)

    val taskToExecute = allTasks.firstOrNull { it.match(taskRaw) }

    if (taskToExecute == null) {
        println("Unknown task!")
        printUsage()
    } else {
        val conditions = buildConditions(args, allConditionFactories)
        if (conditions.isEmpty()) {
            taskToExecute.execute(taskRaw)
        } else {
            InstantExecutor(conditions, Action {
                taskToExecute.execute(taskRaw)
            }).execute()
        }
    }
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

fun buildConditions(args: Array<String>, factories: ArrayList<ConditionFactory>): ArrayList<Condition> {
    val result = ArrayList<Condition>()
    args
            .filter { it.startsWith("-when:") }
            .forEach { argumentRaw: String ->
                val paramsRaw = argumentRaw.substringAfter(':')
                factories.forEach {
                    if (it.match(paramsRaw)) {
                        val factory = it.create(paramsRaw)
                        result.add(factory)
                    }
                }
            }

    return result
}

fun printUsage() {
    println("""
        USAGE:
        -exchange:[exchange] -key:[key] -secret:[secret] -task:[task] [-when:[condition1] -when:[condition2] -when:[condition...]]

        Parameters:
        [exchange] - exchange name
        [key] - exchange api key
        [secret] - exchange api secret
        [task] - task to do (the list to do)
        [when] - optional conditions (all must pass before task is executed), conditions are checked in a 5 minutes loop until program is terminated

        Exchanges:
        -exchange:bitfinex (tested)
        -exchange:binance (testing in process)
        -exchange:bitmarket (in development)

        Tasks:
        -task:wallet - prints list of assets (portfolio/wallet)
        -task:marketbuy([pair]|[value]) - places market buy order on specific pair
        -task:marketsell([pair]|[value]) - places market sell order on specific pair
        -task:pushoveralert([apiKey]|[userId]|[message]) - sends push notification using pushover service

        Conditions:
        RSI
        -when:rsiabove(XRP/USD|70) - observes RSI of XRP/USD pair and completes when RSI > 70
        -when:rsibelow(XRP/USD|30) - observes RSI of XRP/USD pair and completes when RSI < 30
        -when:bestoverboughtrsi(XRP/USD|5) - calculates best overbought RSI of XRP/USD pair and completes when RSI > calculated - 5 (5 is margin/advance)
        -when:bestoversoldrsi(XRP/USD|5) - calculates best oversold RSI of XRP/USD pair and completes when RSI < calculated + 5 (5 is margin/advance)

        CLOSE PRICE
        -when:closepriceabove(XRP/USD|1000) - observes close price of XRP/USD pair and completes when its > 1000)
        -when:closepricebelow(XRP/USD|1000) - observes close price of XRP/USD pair and completes when its < 1000)

        MACD
        -when:macdcrossup(XRP/USD) - observes MACD of XRP/USD pair and completes when the indicator crosses up
        -when:macdcrossdown(XRP/USD) - observes MACD of XRP/USD pair and completes when the indicator crosses down

        MARKETCAP
        -when:marketcapabove(100) - checks if market cap is above 100$
        -when:marketcapabove(100BLN) - checks if market cap is above 100 billions $
        -when:marketcapbelow(100) - checks if market cap is below 100$
        -when:marketcapbelow(100BLN) - checks if market cap is below 100 billions $
    """.trimIndent())
}