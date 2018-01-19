package org.consoletrader.common

import io.reactivex.functions.Action
import org.consoletrader.indicators.LoopPresenter
import java.util.ArrayList

class LoopExecutor(private val conditions: ArrayList<Condition>, private val successAction: Action)  {
    fun execute() {
        val dataSource = EvaluationsDataSource(conditions)
        val presenter = LoopPresenter(this::endLoop, successAction)
        presenter.present(dataSource)
    }

    private fun endLoop(allConditionsPassed:EvaluationResult): Boolean {
        return allConditionsPassed.result
    }
}