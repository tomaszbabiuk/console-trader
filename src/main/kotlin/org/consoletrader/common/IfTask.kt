package org.consoletrader.common

import io.reactivex.functions.Action
import org.consoletrader.indicators.WatchPeriodicDataPresenter
import java.util.ArrayList

class IfTask(private val conditions: ArrayList<Condition>, private val successAction: Action) : Task {
    override fun match(paramsRaw: String): Boolean {
        return true
    }

    override fun execute(paramsRaw: String) {
        val dataSource = WatchersDataSource(conditions)
        val presenter = WatchPeriodicDataPresenter(this::endLoop, successAction)
        presenter.present(dataSource)
    }

    private fun endLoop(allConditionsPassed:Boolean): Boolean {
        return allConditionsPassed
    }
}