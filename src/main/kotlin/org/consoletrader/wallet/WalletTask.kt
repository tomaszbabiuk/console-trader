package org.consoletrader.wallet

import org.consoletrader.common.ExchangeManager
import org.consoletrader.common.Task

class WalletTask(exchangeManager: ExchangeManager) : Task(exchangeManager) {
    override fun match(taskRaw: String): Boolean {
        return taskRaw == "wallet"
    }

    override fun execute(taskRaw: String) {
        val calculator = ListAssetsCalculator(exchangeManager)
        val presenter = ListAssetsResultPresenter()
        presenter.present(calculator)
    }
}