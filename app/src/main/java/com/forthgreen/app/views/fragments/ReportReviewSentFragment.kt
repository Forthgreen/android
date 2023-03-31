package com.forthgreen.app.views.fragments

import android.os.Bundle
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.utils.supportFragmentManager
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_review_report_sent.*

class ReportReviewSentFragment : BaseFragment() {

    companion object {
        const val TAG = "ReportReviewSent"
        private const val BUNDLE_EXTRAS_REVIEW_TYPE = "typeOfReviewReport"

        fun newInstance(isProduct: Boolean = false): ReportReviewSentFragment {
            return ReportReviewSentFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(BUNDLE_EXTRAS_REVIEW_TYPE, isProduct)
                }
            }
        }
    }

    //variables
    private val isProduct by lazy { arguments?.getBoolean(BUNDLE_EXTRAS_REVIEW_TYPE, false)!! }

    override val layoutId: Int
        get() = R.layout.fragment_review_report_sent

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        setUpViews()
        setUpListeners()
    }

    private fun setUpViews() {
        if (isProduct) {
            btnBack.text = getString(R.string.btn_back_product)
        } else {
            btnBack.text = getString(R.string.btn_back_restaurant)
        }

        //push down anim
        PushDownAnim.setPushDownAnimTo(btnBack)
    }

    private fun setUpListeners() {
        btnBack.setOnClickListener {
            repeat(2) {
                supportFragmentManager().popBackStack()
            }
        }

    }
}