package org.consoletrader.common

import io.reactivex.Single

interface Matcher {
    fun match(paramsRaw: String): Boolean
}

interface Task : Matcher {
    fun execute(paramsRaw: String)
}

interface Condition {
    fun buildEvaluator(): Single<EvaluationResult>
}