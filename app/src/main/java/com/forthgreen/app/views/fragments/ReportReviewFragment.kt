package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ReportViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.supportFragmentManager
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_report_review.*
import kotlinx.android.synthetic.main.toolbar.*

class ReportReviewFragment : BaseFragment() {

    companion object {
        const val TAG = "ReportReviewFragment"
        private const val BUNDLE_EXTRAS_REVIEW_REF = "RefForTheSelectedReview"
        private const val BUNDLE_EXTRAS_TYPE = "isSelectedReviewIsOfRestaurantOrProduct"

        fun newInstance(mReviewRef: String = "", isProduct: Boolean = false): ReportReviewFragment {
            return ReportReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_REVIEW_REF, mReviewRef)
                    putBoolean(BUNDLE_EXTRAS_TYPE, isProduct)
                }
            }
        }
    }

    private val reviewRef by lazy { arguments?.getString(BUNDLE_EXTRAS_REVIEW_REF, "")!! }
    private val isProduct by lazy { arguments?.getBoolean(BUNDLE_EXTRAS_TYPE, false)!! }
    private val mReportViewModel: ReportViewModel by lazy {
        ViewModelProvider(this).get(ReportViewModel::class.java)
    }
    override val layoutId: Int
        get() = R.layout.fragment_report_review

    override val viewModel: BaseViewModel?
        get() = mReportViewModel

    override fun init() {
        setUpViews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpViews() {
        tvToolbarTitle.text = getString(R.string.report_abuse_label)
        PushDownAnim.setPushDownAnimTo(btnReport)
    }

    private fun setUpListeners() {
        btnReport.setOnClickListener {
            if (isProduct) {
                mReportViewModel.reportReview(ValueMapping.onReviewReport(), reviewRef)
            } else {
                mReportViewModel.reportReview(ValueMapping.onRestaurantReviewReport(), reviewRef)
            }
        }
    }

    private fun observeProperties() {
        mReportViewModel.onSuccessfulReviewReport().observe(viewLifecycleOwner, Observer {
            if (it) {
                performTransaction(ReportReviewSentFragment.newInstance(isProduct), ReportReviewSentFragment.TAG)
            } else {
                showMessage(message = "You already reported this review.")
            }
        })

    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = false)
    }

}