package org.consoletrader.wallet

import org.consoletrader.common.Calculator
import org.consoletrader.common.ResultPresenter

class ListAssetsResultPresenter : ResultPresenter<PortfolioAsset> {

    override fun present(calculator: Calculator<PortfolioAsset>) {
        var usdSum = 0.0
        calculator
                .calculate()
                .doOnComplete {
                    println("=========================")
                    println("TOTAL $usdSum$")
                }
                .subscribe {
                    println(it)
                    usdSum += it.priceInDollars
                }

    }
}
