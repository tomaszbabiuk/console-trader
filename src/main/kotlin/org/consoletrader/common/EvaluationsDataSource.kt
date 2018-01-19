package org.consoletrader.common

import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class EvaluationsDataSource(conditions: ArrayList<Condition>) : DataSource<EvaluationResult> {
    private val watchersArray = conditions.map { it.buildEvaluator() }.toTypedArray()

    override fun createSingle(): Single<EvaluationResult> {
        return Single.zipArray<EvaluationResult, EvaluationResult>(this::zipper, watchersArray)
    }

    override fun createObservable(): Observable<EvaluationResult> {
        return createSingle().toObservable()
    }

    private fun zipper(source: Array<in EvaluationResult>): EvaluationResult {
        var allTrue = true
        val resultComment = StringBuilder()
        source.forEach {
            if (it is EvaluationResult) {
                if (!it.result) {
                    allTrue = false
                }
                resultComment.appendln(it.comment)
            }
        }
        return EvaluationResult(allTrue, resultComment.toString())
    }
}