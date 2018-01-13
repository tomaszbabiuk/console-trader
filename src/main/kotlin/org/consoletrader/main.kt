package org.consoletrader

import io.reactivex.functions.Action
import org.consoletrader.common.ExchangeMatcher
import org.consoletrader.rsi.RsiCalculator
import org.consoletrader.rsi.RsiResultPresenter
import org.consoletrader.wallet.ListAssetsCalculator
import org.consoletrader.wallet.ListAssetsResultPresenter
import org.knowm.xchange.currency.CurrencyPair


fun main(args: Array<String>) {
    val exchangeName = checkArgument(args, "exchange", "Exchange not defined. Allowed markets: binance, bitfinex")
    val apiKey = checkArgument(args, "key", "API key not defined")
    val apiSecret = checkArgument(args, "secret", "Api secret not defined")
    val task = checkArgument(args, "task", "Calculator not defined. Allowed tasks: wallet, buyonrsibelow, sellonrsiabove")


    if (args.isEmpty() || exchangeName == null || apiKey == null || apiSecret == null || task == null) {
        printUsage()
        return
    }

    val exchangeManager = ExchangeMatcher().match(exchangeName, apiKey, apiSecret)
    if (exchangeManager == null) {
        println("The exchange $exchangeName is not supported")
        return
    }

    when (task) {
        "wallet" -> {
            val calculator = ListAssetsCalculator(exchangeManager)
            val presenter = ListAssetsResultPresenter()
            presenter.present(calculator)
        }
        "rsi" -> {
            val pair = checkArgument(args, "pair", "Market pair not defined. example pairs are: BTC/USD, XRP/ETH, etc.")
            val rsi = checkArgument(args, "rsi", "RSI value to trigger")
            if (pair != null && rsi != null) {
                val calculator = RsiCalculator(exchangeManager, CurrencyPair(pair))
                val successAction = Action {
                    //TODO:execute action by defined in parameters
                    println("success!")
                }
                val presenter = RsiResultPresenter({ it < 30.0 }, successAction)
                presenter.present(calculator)
            }
        }
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