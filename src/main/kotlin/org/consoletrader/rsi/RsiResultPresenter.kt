package org.consoletrader.rsi

import io.reactivex.Observable
import org.consoletrader.common.Calculator
import org.consoletrader.common.ResultPresenter
import java.util.*
import java.util.concurrent.TimeUnit

class RsiResultPresenter(private val conditionFunc: (Double) -> Boolean) : ResultPresenter<Double> {
    override fun present(calculator: Calculator<Double>) {
        Observable
                .interval(0, 30, TimeUnit.SECONDS)
                .map {
                    //                    testing code
//                    if (it > 0) {
//                        29.0
//                    } else {
//                        calculator.calculate().blockingFirst()
//                    }
                    calculator
                            .calculate()
                            .blockingFirst()
                }
                .takeUntil(conditionFunc)
                .doOnComplete {
                    //TODO: buy/sell or fire alert!
                    println("DONE")
                }
                .blockingSubscribe {
                    val now = Date()
                    println("${now.toLocaleString()}: RSI: $it")
                }
    }
}

