package org.consoletrader.common

data class EvaluationResult(val result:Boolean, val comment: String) {
    override fun toString(): String {
        return if (result) {
            "ALL CONDITIONS PASSED\n$comment"
        } else {
            "SOME CONDITIONS ARE FALSE\n$comment"
        }
    }
}