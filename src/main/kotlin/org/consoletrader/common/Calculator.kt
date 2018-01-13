package org.consoletrader.common

import io.reactivex.Observable

interface Calculator<T> {
    fun calculate() : Observable<T>
}