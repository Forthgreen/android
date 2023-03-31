package com.forthgreen.app.views.utils

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 18-May-21
 *
 * @description Custom ViewPager with utility to enable/disable Paging.
 */
class SwipeLockableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    //If true, the paging will be enabled by default.
    private var swipeEnabled = true

    //Overrides and returns false in case of no-paging.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onTouchEvent(event)
            false -> false
        }
    }

    //Overrides and returns false in case of no-paging.
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onInterceptTouchEvent(event)
            false -> false
        }
    }

    //Overrides and returns false in case of no-paging.
    override fun executeKeyEvent(event: KeyEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.executeKeyEvent(event)
            false -> false
        }
    }

    //Call this to change the paging state.
    fun setSwipePagingEnabled(swipeEnabled: Boolean) {
        this.swipeEnabled = swipeEnabled
    }
}