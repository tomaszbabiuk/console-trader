package org.consoletrader.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.ArrayList

class TradedObservable<T>(val source: Observable<T>, val max: Int) : Observable<Observable<ArrayList<T>>>() {
    private var combinedItems: ArrayList<T> = ArrayList()
    private var observer: Observer<in Observable<ArrayList<T>>>? = null
    private var disposable: Disposable? = null

    override fun subscribeActual(observer: Observer<in Observable<ArrayList<T>>>?) {
        this.observer = observer
        disposable = source.subscribe({
            onNext(it)
        }, {
            onComplete()
        })
    }

    fun onNext(item: T) {
        combinedItems.add(item)
        if (combinedItems.size == max) {
            observer?.onNext(just(combinedItems))
            combinedItems.removeAt(0)
        }
    }

    fun onComplete() {
        if (observer != null) {
            observer!!.onComplete()
        }

        if (disposable != null) {
            disposable!!.dispose()
        }
    }
}