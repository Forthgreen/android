package com.forthgreen.app.views.fragments

import android.os.Bundle
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.utils.supportFragmentManager
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_report_sent.*

class ReportSentFragment : BaseFragment() {

    companion object {
        const val TAG = "ReportSentFragment"
        private const val BUNDLE_EXTRAS_TYPE = "BUNDLE_EXTRAS_TYPE"


        fun newInstance(isProfileReported: Boolean = false): ReportSentFragment {
            return ReportSentFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(BUNDLE_EXTRAS_TYPE, isProfileReported)
                }
            }
        }
    }

    // Variables
    private val isProfileReported by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRAS_TYPE, false)
    }

    override val layoutId: Int
        get() = R.layout.fragment_report_sent

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        // Push Down anim
        PushDownAnim.setPushDownAnimTo(btnBack)
        setupListeners()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            if (isProfileReported) {
                repeat(2) {
                    supportFragmentManager().popBackStack()
                }
            } else {
                supportFragmentManager().popBackStack()
            }
        }
    }
}