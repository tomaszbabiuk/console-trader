package org.consoletrader.indicators

import io.reactivex.Observable
import io.reactivex.functions.Action
import org.consoletrader.common.DataSource
import org.consoletrader.common.IndicatorsSet
import org.consoletrader.common.ResultPresenter
import org.slf4j.LoggerFactory
import org.ta4j.core.TimeSeries
import java.util.*
import java.util.concurrent.TimeUnit

class LoopPresenter<T>(private val endLoopFunction: (T) -> Boolean, private val completeAction: Action) : ResultPresenter<T> {
    private val logger = LoggerFactory.getLogger(LoopPresenter::class.java)

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
                    endLoopFunction(it)
                }
                .doOnComplete(completeAction)
                .blockingSubscribe {
                    val now = Date()

                    if (it is TimeSeries) {
                        val indicatorsSet = IndicatorsSet(it)
                        println("[${now.toLocaleString()}]\n$indicatorsSet\n")
                    } else {
                        println("${now.toLocaleString()}: $it")
                    }
                }
    }
}

