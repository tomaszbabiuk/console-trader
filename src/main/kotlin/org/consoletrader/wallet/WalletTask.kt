package org.consoletrader.wallet

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task

class WalletTask(exchangeManager: ExchangeManager) : Task(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw == "wallet"
    }

    override fun execute(paramsRaw: String) {
        val dataSource = ListAssetsDataSource(exchangeManager)
        val presenter = ListAssetsResultPresenter()
        presenter.present(dataSource)
    }
}