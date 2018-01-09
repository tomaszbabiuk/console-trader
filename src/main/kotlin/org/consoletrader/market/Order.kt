package org.consoletrader.market

data class Order(val id: Long, val symbol: String, val amount: Double, val price: Double, val status: String, val type: String, val side: String) {
    override fun toString(): String {
        return "$side $symbol: $amount * $price, $status, $type"
    }
}