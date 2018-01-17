package org.consoletrader.marketcap

import io.reactivex.Observable
import io.reactivex.functions.Action
import org.consoletrader.common.DataSource
import org.consoletrader.common.ResultPresenter
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class WatchMarketcapDataPresenter<T>(private val conditionFunc: (current: T, previous: T?) -> Boolean, private val completeAction: Action) : ResultPresenter<T> {
    private val logger = LoggerFactory.getLogger(WatchMarketcapDataPresenter::class.java)
    var previousData: T? = null

    override fun present(dataSource: DataSource<T>) {
        Observable
                .interval(0, 5, TimeUnit.MINUTES)
                .flatMap {
                    dataSource.createObservable().onErrorResumeNext { throwable: Throwable ->
                        logger.error("Problem getting data", throwable)
                        Observable.empty<T>()
                    }
                }
                .takeUntil {
                    conditionFunc(it, previousData)
                }
                .doOnComplete(completeAction)
                .blockingSubscribe {
                    previousData = it
                    val now = Date()
                    println("${now.toLocaleString()}: $it")
                }
    }
}

