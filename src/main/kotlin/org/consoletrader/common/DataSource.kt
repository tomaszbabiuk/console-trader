package org.consoletrader.common

import io.reactivex.Observable

interface DataSource<T> {
    fun createObservable() : Observable<T>
}