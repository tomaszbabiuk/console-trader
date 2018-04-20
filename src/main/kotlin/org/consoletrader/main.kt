package org.consoletrader

import org.consoletrader.common.*
import org.consoletrader.exchange.ExchangeMatcher
import org.consoletrader.indicators.*
import org.consoletrader.marketcap.*
import org.consoletrader.notifications.pushover.PushoverNotificationTask
import org.consoletrader.orders.MarketBuyTask
import org.consoletrader.orders.MarketSellTask
import org.consoletrader.roi.CalculateStopPriceTask
import org.consoletrader.analyse.AnalyseTask
import org.consoletrader.notifications.gmail.GmailAlertTask
import org.consoletrader.orders.CancelOrdersTask
import org.consoletrader.orders.TrailingStopTask
import org.consoletrader.roi.RoiAboveTask
import org.consoletrader.roi.RoiBelowTask
import org.consoletrader.tax.TaxTask
import org.consoletrader.wallet.*

fun main(args: Array<String>) {
    val exchangeName = checkArgument(args, "exchange", "Exchange not defined. Allowed markets: binance, bitfinex")
    val apiKey = checkArgument(args, "key", "API key not defined")
    val apiSecret = checkArgument(args, "secret", "Api secret not defined")
    val taskRaw = checkArgument(args, "task", "Task not defined.")

    if (args.isEmpty() || exchangeName == null || apiKey == null || apiSecret == null || taskRaw == null) {
        printUsage()
        return
    }

    val exchangeManager = ExchangeMatcher().match(exchangeName, apiKey, apiSecret)
    if (exchangeManager == null) {
        println("The exchange $exchangeName is not supported")
        return
    }

    val allTasks = ArrayList<Task>()
    allTasks += MarketCapAboveTask()
    allTasks += MarketCapBelowTask()
    allTasks += PushoverNotificationTask()
    allTasks += GmailAlertTask()

    allTasks += WalletTask(exchangeManager)
    allTasks += MarketBuyTask(exchangeManager)
    allTasks += MarketSellTask(exchangeManager)
    allTasks += TrailingStopTask(exchangeManager)
    allTasks += AnalyseTask(exchangeManager)
    allTasks += CalculateStopPriceTask(exchangeManager)
    allTasks += BalanceAboveTask(exchangeManager)
    allTasks += BalanceBelowTask(exchangeManager)
    allTasks += BestOverboughtRsiTask(exchangeManager)
    allTasks += BestOversoldRsiTask(exchangeManager)
    allTasks += RsiAboveTask(exchangeManager)
    allTasks += RsiBelowTask(exchangeManager)
    allTasks += RoiAboveTask(exchangeManager)
    allTasks += RoiBelowTask(exchangeManager)
    allTasks += ClosePriceAboveTask(exchangeManager)
    allTasks += ClosePriceBelowTask(exchangeManager)
    allTasks += MacdCrossDownTask(exchangeManager)
    allTasks += MacdCrossUpTask(exchangeManager)
    allTasks += CancelOrdersTask(exchangeManager)
    allTasks += BollingerEdgeAboveTask(exchangeManager)
    allTasks += BollingerEdgeBelowTask(exchangeManager)
    allTasks += BollingerWidthAboveTask(exchangeManager)
    allTasks += BollingerWidthBelowTask(exchangeManager)
    allTasks += MfiAboveTask(exchangeManager)
    allTasks += MfiBelowTask(exchangeManager)
    allTasks += MfiVPatternTask(exchangeManager)
    allTasks += MfiBottomTask(exchangeManager)
    allTasks += TaxTask(exchangeManager)

    val taskToExecute = allTasks.firstOrNull { it.match(taskRaw) }

    if (taskToExecute == null) {
        println("Unknown task!")
        printUsage()
    } else {
        taskToExecute.execute(taskRaw)
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

fun printUsage() {
    println("""
        USAGE:
        -exchange:[exchange] -key:[key] -secret:[secret] -task:[task]

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
        -task:trailingstop([pair]|[value]|[distance]) - places trailing stop order on specific pair (only for Bitfinex)
        -task:cancelorders([pair]) - cancels all orders on specific pair

        -task:pushoveralert([apiKey]|[userId]|[message]) - sends push notification using pushover service

        -task:marketcapabove([pair]|[threshold]) - exits 0 if market cap value is above threshold, otherwise exits 1
        -task:marketcapabelow([pair]|[threshold]) - exits 0 if market cap value is below threshold, otherwise exits 1

        -task:balanceabove([currency]|[threshold]) - exits 0 if balance of specified currency is above threshold, otherwise exits 1
        -task:balanceabelow([currency]|[threshold]) - exits 0 if balance of specified currency is below threshold, otherwise exits 1

        -task:rsiabove([currency]|[threshold]) - exits 0 if RSI of specified currency is above threshold, otherwise exits 1
        -task:rsibelow([currency]|[threshold]) - exits 0 if RSI of specified currency is below threshold, otherwise exits 1

        -task:bollingerabove([currency]) - exits 0 if close price is above upper Bollinger band, otherwise exits 1
        -task:bollingerbelow([currency]) - exits 0 if close price is below lower Bollinger band, otherwise exits 1

        -task:macdcrossup([pair]) - exits 0 if MACD crosses up, otherwise exits 1
        -task:macdcrossdown([pair]) - exits 0 if MACD crosses down, otherwise exits 1

        -task:bestoversoldrsi([pair]|[rsi advance]|[min short gain]|[min loss]) - exits 0 if current rsi of specified pair is below best calculated overbought rsi value (plus "rsi advance" as a correction), otherwise exits 1
              you can also provide min short gain (in percent) and min loss (also in percent) in order to deepen the analysis
        -task:bestoverboughtrsi([pair]|[rsi advance]) - exits 0 if current rsi of specified pair is above best calculated oversold rsi value (minus "rsi advance" as correction), otherwise exits 1

        -task:roiabove([pair]|[last buying amount to consider]|[return of investment threshold] - exits 0 if return of investment is above threshold, otherwise return 1
        -task:roibelow([pair]|[last buying amount to consider]|[return of investment threshold] - exits 0 if return of investment is below threshold, otherwise return 1

    """.trimIndent())
}