package org.consoletrader.common

import io.reactivex.functions.Action
import java.util.ArrayList

class InstantExecutor(private val conditions: ArrayList<Condition>, private val successAction: Action)  {
    fun execute() {
        val dataSource = EvaluationsDataSource(conditions)
        dataSource
                .createObservable()
                .blockingSubscribe {
                    if (it.result) {
                        successAction.run()
                    }
                }
    }
}