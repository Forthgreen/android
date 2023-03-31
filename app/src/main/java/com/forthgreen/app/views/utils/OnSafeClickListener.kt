package com.forthgreen.app.views.utils

import android.os.SystemClock
import android.view.View
import com.forthgreen.app.views.interfaces.OnSafeClickListener

/**
 * Created by Chetan Tuteja on 2019-11-27.
 */
abstract class SafeClickListener : View.OnClickListener {

    //Default time difference to observe on between clicks. [1 Second in this case]
    private var defaultInterval: Int = 1000

    //To keep track of last click
    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        //If the time between clicks is less than default interval then do nothing else perform click.
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        performClick(v)
    }

    abstract fun performClick(v: View)
}


//Extension function to offer setting click as a lambda and then overriding and performing click.
fun View.setOnSafeClickListener(onSafeClick: (View) -> Unit) {
    setOnClickListener(object : SafeClickListener() {
        override fun performClick(v: View) {
            onSafeClick(v)
        }

    })
}

//Extension function to make use of interface to perform clicks in case of multiple safeclick implementations.
fun View.setOnSafeClickListener(l: OnSafeClickListener) {
    setOnClickListener(object : SafeClickListener() {
        override fun performClick(v: View) {
            l.onSafeClick(v)
        }
    })
}