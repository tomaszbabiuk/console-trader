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
import org.consoletrader.analyse.AnalyseTask
import org.consoletrader.bash.ExitTask
import org.consoletrader.strategy.MatchStrategyTask
import org.consoletrader.wallet.BalanceBelowConditionFactory
import org.consoletrader.wallet.BalanceAboveConditionFactory
import org.consoletrader.wallet.WalletTask
import kotlin.system.exitProcess


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
    allConditionFactories.add(MacdCrossUpConditionFactory(exchangeManager))
    allConditionFactories.add(MacdCrossDownConditionFactory(exchangeManager))
    allConditionFactories.add(ClosePriceAboveConditionFactory(exchangeManager))
    allConditionFactories.add(ClosePriceBelowConditionFactory(exchangeManager))
    allConditionFactories.add(BestOversoldRsiConditionFactory(exchangeManager))
    allConditionFactories.add(BestOverboughtRsiConditionFactory(exchangeManager))
    allConditionFactories.add(ProfitConditionFactory(exchangeManager))
    allConditionFactories.add(BalanceAboveConditionFactory(exchangeManager))
    allConditionFactories.add(BalanceBelowConditionFactory(exchangeManager))

    val allTasks = ArrayList<Task>()
    allTasks += WalletTask(exchangeManager)
    allTasks += MarketBuyTask(exchangeManager)
    allTasks += MarketSellTask(exchangeManager)
    allTasks += MatchStrategyTask(exchangeManager)
    allTasks += PushoverNotificationTask(exchangeManager)
    allTasks += AnalyseTask(exchangeManager)
    allTasks += CalculateStopPriceTask(exchangeManager)
    allTasks += MarketCapAboveTask()
    allTasks += MarketCapBelowTask()
    allTasks += ExitTask()

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
            }, Action {
                exitProcess(1)
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
                var conditionMatched = false
                factories.forEach {
                    if (it.match(paramsRaw)) {
                        val factory = it.create(paramsRaw)
                        result.add(factory)
                        conditionMatched = true
                    }
                }

                if (!conditionMatched) {
                    println("WARNING: unknown argument $argumentRaw - ignoring")
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
        -task:marketcapabove([pair]|[threshold]) - exits 0 if marketcap value is above threshold, otherwise exits 1

    """.trimIndent())
}