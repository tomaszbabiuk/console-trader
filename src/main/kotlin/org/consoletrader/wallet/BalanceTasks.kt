package org.consoletrader.wallet

import io.reactivex.Single
import org.consoletrader.common.DataSourceTask
import org.consoletrader.common.ExchangeManager


abstract class BalanceTask(val exchangeManager: ExchangeManager) : DataSourceTask<PortfolioAsset, CurrencyAndValueExtendedParams>() {
    override fun createDataSource(params: CurrencyAndValueExtendedParams): Single<PortfolioAsset> {
        return ListAssetsDataSource(exchangeManager).create()
                .toObservable()
                .flatMapIterable { it }
                .filter {
                    it.assetSymbol == params.currency.symbol
                }
                .firstOrError()
    }

    override fun createParams(paramsRaw: String): CurrencyAndValueExtendedParams {
        return CurrencyAndValueExtendedParams(paramsRaw)
    }
}

class BalanceAboveTask(exchangeManager: ExchangeManager) : BalanceTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("balanceabove")
    }

    override fun verifySuccess(data: PortfolioAsset, params: CurrencyAndValueExtendedParams): Boolean {
        return data.amount > params.value
    }
}

class BalanceBelowTask(exchangeManager: ExchangeManager) : BalanceTask(exchangeManager) {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("balancebelow")
    }

    override fun verifySuccess(data: PortfolioAsset, params: CurrencyAndValueExtendedParams): Boolean {
        return data.amount < params.value
    }
}