package org.consoletrader.marketcap

class Marketcap(val value:Double) {
    override fun toString(): String {
        return "$${value/10e8} BLN"
    }
}