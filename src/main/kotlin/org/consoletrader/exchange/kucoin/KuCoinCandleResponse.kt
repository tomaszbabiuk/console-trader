package org.consoletrader.exchange.kucoin

data class KuCoinCandleResponse(val t: ArrayList<Long>,
                                val o: ArrayList<Double>,
                                val h: ArrayList<Double>,
                                val l: ArrayList<Double>,
                                val c: ArrayList<Double>,
                                val v: ArrayList<Double>
)