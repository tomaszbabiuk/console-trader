package org.consoletrader.common

import io.reactivex.functions.Action
import org.consoletrader.indicators.LoopPresenter
import java.util.ArrayList

class IfTask(private val conditions: ArrayList<Condition>, private val successAction: Action) : Task {
    override fun match(paramsRaw: String): Boolean {
        return true
    }

    override fun execute(paramsRaw: String) {
        val dataSource = EvaluationsDataSource(conditions)
        val presenter = LoopPresenter(this::endLoop, successAction)
        presenter.present(dataSource)
    }

    private fun endLoop(allConditionsPassed:EvaluationResult): Boolean {
        return allConditionsPassed.result
    }
}