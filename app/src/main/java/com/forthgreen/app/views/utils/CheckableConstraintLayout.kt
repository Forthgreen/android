package com.forthgreen.app.views.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable

/**
 * Created by ShrayChona on 06-10-2018.
 */
class CheckableConstraintLayout(context: Context, attrs: AttributeSet) : androidx.constraintlayout.widget.ConstraintLayout(context, attrs), Checkable {

    private var mChecked = false

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(b: Boolean) {
        if (b != mChecked) {
            mChecked = b
            refreshDrawableState()
        }
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    companion object {

        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}