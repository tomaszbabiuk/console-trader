package org.consoletrader.common

import io.reactivex.Single
import kotlin.system.exitProcess

abstract class DataSourceTask<T, P> : Task {
    override fun execute(paramsRaw: String) {
        val params = createParams(paramsRaw)
        createDataSource(params)
                .doOnSuccess {
                    println(it)

                    val result = verifySuccess(it, params)
                    if (result) {
                        exitProcess(0)
                    } else {
                        exitProcess(1)
                    }
                }
                .doOnError {
                    println(it)
                    exitProcess(1)
                }
                .blockingGet()
    }

    abstract fun verifySuccess(data: T, params: P): Boolean

    abstract fun createDataSource(params: P): Single<T>

    abstract fun createParams(paramsRaw: String): P
}