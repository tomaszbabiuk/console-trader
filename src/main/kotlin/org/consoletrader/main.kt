package org.consoletrader

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeMatcher
import org.consoletrader.common.Task
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

    val tasks = ArrayList<Task>()
    tasks += WalletTask(exchangeManager)
    tasks += WatchRsiAboveTask(exchangeManager, Action { println("Yupi... RSI above")})
    tasks += WatchRsiBelowTask(exchangeManager, Action { println("Yupi... RSI below")})

    tasks
        .filter { it.match(taskRaw) }
        .forEach { it.execute(taskRaw) }
}


//private fun processBuyWhenRsiBelow(exchangeManager: ExchangeManager, args: Array<String>) {
//    val pair = checkArgument(args, "pair", "Market pair not defined. example pairs are: BTC/USD, XRP/ETH, etc.")
//    val amount = checkArgument(args, "amount", "Amount not defined!")
//    val rsi = checkArgument(args, "rsi", "RSI value to trigger")
//
//    if (pair != null && rsi != null && amount != null) {
//        val pairParsed = CurrencyPair(pair)
//        val amountInBaseCurrency = amount.endsWith(pairParsed.base.toString(), true)
//        val amountInCounterCurrency = amount.endsWith(pairParsed.counter.toString(), true)
//
//        if (amountInBaseCurrency || amountInCounterCurrency) {
//            val rsiParsed = rsi.toDouble()
//            val calculator = RsiCalculator(exchangeManager, pairParsed)
//            val successAction = Action {
//                println("RSI is below target, placing market order to buy some!")
//                val tradeService = exchangeManager.exchange.tradeService
//                val amountParsed = amount
//                        .replace(pairParsed.base.toString(),"")
//                        .replace(pairParsed.counter.toString(), "")
//                        .toBigDecimal()
//
//                if (amountInCounterCurrency) {
//                    val tickerPrice = exchangeManager.exchange.marketDataService.getTicker(pairParsed).bid * BigDecimal.valueOf(1.05)
//                    val amountToBuy: BigDecimal = amountParsed.divide(tickerPrice, 2, RoundingMode.DOWN)
//                    println("Trying to buy $amountToBuy${pairParsed.base} for about $amountParsed${pairParsed.counter} ")
//                    tradeService.placeMarketOrder(MarketOrder(Order.OrderType.BID, amountToBuy, pairParsed))
//                } else {
//                    println("Trying to buy $amountParsed${pairParsed.base}")
//                    tradeService.placeMarketOrder(MarketOrder(Order.OrderType.BID, amountParsed, pairParsed))
//                }
//            }
//            val presenter = RsiResultPresenter({ it < rsiParsed }, successAction)
//            presenter.present(calculator)
//        } else {
//            println("The amount should be in base or counter currency, eg: 10 ${pairParsed.base} or 15${pairParsed.counter}")
//        }
//    }
//}

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
    println("USAGE:")
    println("-exchange:[exchange] -key:[key] -secret:[secret] -task:wallet")
    println("-exchange:[exchange] -key:[key] -secret:[secret] -task:watchrsibelow(XRP/USD|30) -action:marketbuy(XRP/USD|10XRP)")

    //TODO:
    //-task:gmailalert(gmailusername|gmailpassword)
    //-task:pushoveralert(username|apikey)
    //-condition:rsibelow(XRP/USD|30)
    //-condition:rsiabove(XRP/USD|70)
}