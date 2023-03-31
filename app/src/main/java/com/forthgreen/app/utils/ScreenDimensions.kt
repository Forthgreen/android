package com.forthgreen.app.utils

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

/**
 * Created by ShrayChona on 03-10-2018.
 * @description this class is used for getting screen dimensions
 * @param mContext {Context}
 */
open class ScreenDimensions(private val mContext: Context) {

    companion object {
        private var screenWidth: Int = 0
        private var screenHeight: Int = 0
    }

    /**
     *  @description get screen width
     */
    fun getScreenWidth(): Int {
        return if (0 == screenWidth) {
            val display = (mContext
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val size = Point()
            display.getSize(size)
            screenWidth = size.x
            screenWidth
        } else {
            screenWidth
        }
    }

    /**
     *  @description get screen height
     */
    fun getScreenHeight(): Int {
        return if (0 == screenHeight) {
            val display = (mContext
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val size = Point()
            display.getSize(size)
            screenHeight = size.y
            screenHeight
        } else {
            screenHeight
        }
    }

    /**
     *  @description get screen height with percentage
     *  @param percentage {Int}
     */
    fun getHeightWithGivenPercentage(percentage: Int): Int {
        if (0 == screenHeight) {
            screenHeight = getScreenHeight()
        }
        return percentage * screenHeight / 100
    }

}