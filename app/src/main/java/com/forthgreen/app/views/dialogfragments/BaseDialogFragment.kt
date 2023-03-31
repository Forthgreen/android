package com.forthgreen.app.views.dialogfragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.forthgreen.app.R
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by ShrayChona on 03-10-2018.
 * @description extend this class for basic dialogFragment setup
 */
abstract class BaseDialogFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, R.style.AppTheme)

        // making fragment dialog transparent and of fullscreen width
        val dialog = dialog
        if (null != dialog) {
            if (isFullScreenDialog) {
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
            } else {
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            dialog.window!!.setWindowAnimations(R.style.DialogFragmentAnimations)
        }

        // set toolbar
        if (null != toolbar) {
            toolbar.setNavigationIcon(R.drawable.ic_cross)
            toolbar.setNavigationOnClickListener { dismiss() }
        }

        init()
    }

    abstract val isFullScreenDialog: Boolean

    abstract val layoutId: Int

    abstract fun init()
}