package org.consoletrader.common

interface ResultPresenter<T>  {
    fun present(dataSource: DataSource<T>)
}