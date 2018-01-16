package org.consoletrader.indicators

import io.reactivex.Observable
import io.reactivex.functions.Action
import org.consoletrader.common.DataSource
import org.consoletrader.common.ResultPresenter
import org.slf4j.LoggerFactory
import org.ta4j.core.TimeSeries
import java.util.*
import java.util.concurrent.TimeUnit

class IndicatorResultPresenter(private val conditionFunc: (TimeSeries) -> Boolean, private val completeAction: Action) : ResultPresenter<TimeSeries> {
    private val logger = LoggerFactory.getLogger(IndicatorResultPresenter::class.java)

    override fun present(dataSource: DataSource<TimeSeries>) {
        Observable
                .interval(0, 5, TimeUnit.MINUTES)
                .map {
                    try {
                        val data = dataSource
                                .createObservable()
                                .blockingFirst()
                        Optional.of(data)
                    } catch (ex: Exception) {
                        logger.error("Problem getting data from exchange", ex)
                        Optional.empty<TimeSeries>()
                    }
                }
                .takeUntil {
                    if (it.isPresent) {
                        conditionFunc(it.get())
                    } else {
                        false
                    }
                }
                .doOnComplete(completeAction)
                .blockingSubscribe {
                    if (it.isPresent) {
                        val now = Date()
                        val timeSeries = it.get()
                        val indicatorsSet = IndicatorsSet(timeSeries)
                        println("[${now.toLocaleString()}]\n$indicatorsSet\n")
                    }
                }
    }
}

