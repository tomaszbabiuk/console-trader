package org.consoletrader.common

interface ResultPresenter<T>  {
    fun present(calculator: Calculator<T>)
}