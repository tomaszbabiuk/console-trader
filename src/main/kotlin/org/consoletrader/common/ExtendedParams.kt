package org.consoletrader.common

open class ExtendedParams {

    protected fun splitParameters(params: String): List<String> {
        val taskParamsRaw = params.substring(params.indexOf("(") + 1, params.indexOf(")"))
        return taskParamsRaw.split("|")
    }
}