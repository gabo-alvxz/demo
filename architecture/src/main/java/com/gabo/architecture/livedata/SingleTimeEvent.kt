package com.gabo.architecture.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 *  A LiveData that will only notify one single Observer per event.
 *  This must be used for UI - related events so we avoid performing an action multiple
 *  times on configuration changes.
 *
 *  For example we use it for the SearchParameters updated events to tell the  SearchResultsFragment
 *  that the user has changed the filters or the search query.
 *
 *  We don't want the screen rotation to trigger a new search. But we do want it to trigger the LiveData
 *  with the results.
 *
 */
class SingleTimeEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(true)

    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, { t ->
            if(pending.compareAndSet(true, false)){
                Timber.i("onChanged")
                observer.onChanged(t)
            }
        })
    }

    override fun postValue(value: T) {
        pending.set(true)
        super.postValue(value)
    }

}