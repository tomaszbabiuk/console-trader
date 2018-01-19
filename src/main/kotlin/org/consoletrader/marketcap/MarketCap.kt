package org.consoletrader.marketcap

class MarketCap(val value:Double) {
    override fun toString(): String {
        return "$${value/10e8} BLN"
    }
}