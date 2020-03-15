package com.lion4ik.github

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.nonNullObserve(lifecycleOwner: LifecycleOwner, observer: (t: T) -> Unit) {
    observe(lifecycleOwner, Observer {
        it?.let(observer)
    })
}
