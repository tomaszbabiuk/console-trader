package org.consoletrader.wallet

import org.consoletrader.common.DataSource
import org.consoletrader.common.ResultPresenter

class ListAssetsResultPresenter : ResultPresenter<MutableList<PortfolioAsset>> {

    override fun present(dataSource: DataSource<MutableList<PortfolioAsset>>) {
        dataSource
                .create()
                .doOnSuccess {
                    var usdSum = 0.0
                    it.forEach {
                        usdSum += it.priceInDollars
                        println(it)
                    }
                    println("=========================")
                    println("TOTAL $usdSum$")
                }
                .blockingGet()
    }
}
