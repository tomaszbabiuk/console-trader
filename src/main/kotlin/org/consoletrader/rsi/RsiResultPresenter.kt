package org.consoletrader.rsi

import io.reactivex.Observable
import io.reactivex.functions.Action
import org.consoletrader.common.Calculator
import org.consoletrader.common.ResultPresenter
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RsiResultPresenter(private val conditionFunc: (Double) -> Boolean, private val completeAction: Action) : ResultPresenter<Double> {
    private val logger = LoggerFactory.getLogger(RsiResultPresenter::class.java)

    override fun present(calculator: Calculator<Double>) {
        Observable
                .interval(0, 5, TimeUnit.MINUTES)
                .map {
                    try {
                        val rsi = calculator
                                .calculate()
                                .blockingFirst()
                        Optional.of(rsi)
                    } catch (ex: Exception) {
                        logger.error("Problem getting data from exchange", ex)
                        Optional.empty<Double>()
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
                        println("${now.toLocaleString()}: RSI: ${it.get()}")
                    }
                }
    }
}

