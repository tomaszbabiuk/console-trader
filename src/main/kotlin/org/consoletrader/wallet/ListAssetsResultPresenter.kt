package org.consoletrader.wallet

import org.consoletrader.common.DataSource
import org.consoletrader.common.ResultPresenter

class ListAssetsResultPresenter : ResultPresenter<PortfolioAsset> {

    override fun present(dataSource: DataSource<PortfolioAsset>) {
        var usdSum = 0.0
        dataSource
                .createObservable()
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
