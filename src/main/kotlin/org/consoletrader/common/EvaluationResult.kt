package org.consoletrader.common

data class EvaluationResult(val result:Boolean, val comment: String) {
    override fun toString(): String {
        return comment
    }
}