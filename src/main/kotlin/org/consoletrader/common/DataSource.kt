package org.consoletrader.common

import io.reactivex.Observable
import io.reactivex.Single

interface DataSource<T> {
    fun createObservable() : Observable<T>
    fun createSingle() : Single<T>
}