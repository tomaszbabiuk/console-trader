package org.consoletrader.tasks.binance

import com.binance.api.client.BinanceApiClientFactory
import com.binance.api.client.BinanceApiRestClient
import org.consoletrader.tasks.Task
import java.util.*

abstract class BaseBinanceOrder(val apiKey: String, val apiSecret: String) : Task {
    override fun execute() {
        val factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret)
        val client = factory.newRestClient()
        val serverTime = client.serverTime
        println("Connected to Binance, server time is ${Date(serverTime)}")
        execute(client)
    }

    abstract fun execute(client: BinanceApiRestClient)
}