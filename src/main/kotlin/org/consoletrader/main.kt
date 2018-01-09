package org.consoletrader

import org.consoletrader.market.Candle
import org.consoletrader.tasks.binance.ClearAllOrdersTask
import org.consoletrader.tasks.ListAssetsTask
import org.consoletrader.tasks.binance.OpenOrdersTask
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.bitfinex.v1.BitfinexExchange


fun main(args: Array<String>) {
    val market = checkArgument(args, "market", "Market not defined. Allowed markets: binance")
    val apiKey = checkArgument(args, "key", "API key not defined")
    val apiSecret = checkArgument(args, "secret", "Api secret not defined")
    val task = checkArgument(args, "task", "Task not defined. Allowed tasks: cancelorders, listorders, buy")

    if (args.isEmpty() || market == null || apiKey == null || apiSecret == null || task == null) {
        printUsage()
        return
    }

    val exSpec = BitfinexExchange().defaultExchangeSpecification
    exSpec.apiKey = apiKey
    exSpec.secretKey = apiSecret
    val exchange = ExchangeFactory.INSTANCE.createExchange(exSpec)


    when (task) {
        "listorders" -> OpenOrdersTask(apiKey, apiSecret).execute()
        "listassets" -> ListAssetsTask(exchange).execute()
        "clearorders" -> ClearAllOrdersTask(apiKey, apiSecret).execute()
        else -> {
            println("Unknown task!")
        }
    }

//    println("Creating binance client... please wait")
//    val factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret)
//    val client = factory.newRestClient()
//    val serverTime = client.serverTime
//    println("Binance client ping back, server time is ${Date(serverTime)}")


//    val allPrices = client.allPrices
//    println(allPrices)
//
//    for (x in allPrices) {
//        println(x)
//       getRSIForSymbol(client, x)
//    }


//    val candlesProcessed = TreeMap<Long, Candle>()
//
//    val candlesticks = client.getCandlestickBars("XRPETH", CandlestickInterval.HALF_HOURLY)
//
//    val candlesticksObservable = Observable
//            .just(candlesticks)
//            .flatMapIterable { it }
//            .takeLast(100)
//            .map { x ->
//                Candle(x.closeTime, x.open.toDouble(), x.close.toDouble(), x.high.toDouble(), x.low.toDouble())
//            }
}

//fun getRSIForSymbol(client: BinanceApiRestClient, price: TickerPrice) {
//    val candlesticks = client.getCandlestickBars(price.symbol, CandlestickInterval.HALF_HOURLY)
//    var ath = 0.0
//
//    if (candlesticks.size > 499) {
//        val candlesticksObservable = Observable
//                .just(candlesticks)
//                .flatMapIterable { it }
//                .doOnNext {
//                    val p = it.close.toDouble()
//                    if (p > ath) {
//                        ath = p
//                    }
//                }
//                .takeLast(14)
//                .map { x ->
//                    Candle(x.closeTime, x.open.toDouble(), x.close.toDouble(), x.high.toDouble(), x.low.toDouble())
//                }
//
//        val sourceForRSI = TradedObservable<Candle>(candlesticksObservable, 14)
//        sourceForRSI.subscribe {
//            it.subscribe {
//                val rsi = calculateRSI(it)
//                println("${price.symbol} ${price.price} $rsi")
//                if (rsi <= 30) {
//                    val gain: Double = ath / price.price.toDouble()
//                    println("Consider buying ${price.symbol}!, ath = $ath, ath/price=$gain")
//                    println()
//                }
//            }
//        }
//    }
//}


//fun calculateEMA(it: List<Candle>): Double {
//    var sum = 0.0
//
//    for (x: Candle in it) {
//        sum += x.close
//    }
//
//    return sum / it.size
//}
//
//fun calculateSignel(it: List<Candle>): Double {
//    var sum = 0.0
//
//    for (x: Candle in it) {
//        sum += x.macd
//    }
//
//    return sum / it.size
//}

fun calculateRSI(it: List<Candle>): Double {
    var bullishCandles = 0.0
    var bearishCandles = 0.0

    it
            .map { it.close < it.open }
            .forEach {
                if (it) {
                    bearishCandles++
                } else {
                    bullishCandles++
                }
            }

    val rs: Double = bullishCandles / bearishCandles
    return 100 - (100 / (1 + rs))
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
    println("-market:[market] -key:[key] -secret:[secret] -task:listassets")
    println("-market:[market] -key:[key] -secret:[secret] -task:listorders")
    println("-market:[market] -key:[key] -secret:[secret] -task:clearorders")
//    println("-market:[market] -key:[key] secret:[secret] -task:buy -asset:[asset] -onrsibelow:[rsi]")
}