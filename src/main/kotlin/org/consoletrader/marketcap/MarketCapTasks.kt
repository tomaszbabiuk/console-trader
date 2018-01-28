package org.consoletrader.marketcap

import org.consoletrader.common.Task
import org.consoletrader.marketcap.coinmarketcap.CoinmarketcapMarketcapService
import kotlin.system.exitProcess

abstract class MarketCapTask : Task {
    private val marketCapService = CoinmarketcapMarketcapService()

    override fun execute(paramsRaw: String) {
        val params = MarketCapExtendedParams(paramsRaw)
        marketCapService
                .createSingle()
                .doOnSuccess {
                    println(it)

                    if (checkMarketCap(it, params.amount)) {
                        exitProcess(0)
                    } else {
                        exitProcess(1)
                    }
                }
                .doOnError {
                    println(it)
                    exitProcess(1)
                }
                .blockingGet()
    }

    abstract fun checkMarketCap(it: MarketCap, threshold: Double): Boolean
}


class MarketCapAboveTask : MarketCapTask() {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapabove")
    }

    override fun checkMarketCap(it: MarketCap, threshold: Double): Boolean {
        return it.value > threshold
    }
}

class MarketCapBelowTask: MarketCapTask() {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("marketcapbelow")
    }

    override fun checkMarketCap(it: MarketCap, threshold: Double): Boolean {
        return it.value < threshold
    }
}

