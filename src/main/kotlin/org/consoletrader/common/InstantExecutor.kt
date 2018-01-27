package org.consoletrader.common

import io.reactivex.functions.Action
import java.util.*

class InstantExecutor(private val conditions: ArrayList<Condition>, private val successAction: Action, private val failureAction: Action)  {
    fun execute() {
        val dataSource = EvaluationsDataSource(conditions)
        dataSource
                .createObservable()
                .blockingSubscribe {
                    println("$it")

                    if (it.result) {
                        successAction.run()
                    } else {
                        failureAction.run()
                    }
                }
    }
}