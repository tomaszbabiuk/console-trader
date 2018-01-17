package org.consoletrader.marketcap.coinmarketcap

import com.google.gson.annotations.SerializedName

data class CoinmarketcapGlobalResult(@SerializedName("total_market_cap_usd") val totalMarketCapUsd: Double)