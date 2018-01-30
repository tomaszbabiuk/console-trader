package org.consoletrader.common

import io.reactivex.Single

interface DataSource<T> {
    fun create() : Single<T>
}