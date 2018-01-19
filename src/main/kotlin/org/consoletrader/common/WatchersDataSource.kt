package org.consoletrader.common

import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class WatchersDataSource(conditions: ArrayList<Condition>) : DataSource<Boolean> {
    private val watchersArray = conditions.map { it.buildEvaluator() }.toTypedArray()

    override fun createSingle(): Single<Boolean> {
        return Single.zipArray<Boolean, Boolean>(this::zipper, watchersArray)
    }

    override fun createObservable(): Observable<Boolean> {
        return createSingle().toObservable()
    }

    private fun zipper(source: Array<in Boolean>): Boolean {
        var allTrue = true
        source.forEach {
            if (it == false) {
                allTrue = false
            }
        }
        return allTrue
    }
}