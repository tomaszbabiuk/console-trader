package org.consoletrader.market

data class Order(val symbol: String, val amount: Double, val price: Double, val status: String, val type: String, val side: String) {
    override fun toString(): String {
        return "$side $symbol: $amount * $price, $status, $type"
    }
}