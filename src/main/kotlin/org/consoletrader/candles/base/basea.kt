package org.consoletrader.candles.base

import retrofit2.Retrofit

abstract class basea<T>(x: Class<T>) {

    protected val api: T


    init {


        api = Retrofit.Builder().build().create(x)
    }
}
