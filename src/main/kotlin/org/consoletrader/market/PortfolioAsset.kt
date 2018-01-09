package org.consoletrader.market

data class PortfolioAsset(val assetSymbol: String, val amount: Double, val priceInDollars: Double) {
    override fun toString(): String {
        return "$assetSymbol: ${"%.8f".format(amount)} = ${"%.2f".format(priceInDollars)}$"
    }
}