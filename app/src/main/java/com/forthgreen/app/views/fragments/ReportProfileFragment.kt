package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ReportProfileViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_report_profile.*
import kotlinx.android.synthetic.main.toolbar.*

class ReportProfileFragment : BaseFragment() {

    companion object {
        const val TAG = "ReportProfileFragment"
        private const val BUNDLE_EXTRAS_USER_REF = "BUNDLE_EXTRAS_USER_REF"

        fun newInstance(mRef: String): ReportProfileFragment {
            return ReportProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_USER_REF, mRef)
                }
            }
        }
    }

    private val userRef by lazy {
        requireArguments().getString(BUNDLE_EXTRAS_USER_REF, "")
    }
    private val mReportProfileViewModel by lazy {
        ViewModelProvider(this).get(ReportProfileViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_report_profile

    override val viewModel: BaseViewModel?
        get() = mReportProfileViewModel

    override fun init() {
        setUpViews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpViews() {
        // Set up toolbar title
        tvToolbarTitle.text = getString(R.string.report_profile_toolbar_title)

        // Push down anim
        PushDownAnim.setPushDownAnimTo(btnReport)
    }

    private fun setUpListeners() {
        btnReport.setOnClickListener {
            when (rbContainer.checkedRadioButtonId) {
                R.id.rbReportNonVegan -> {
                    reportUserProfile(mUserReportType = ValueMapping.onNonVeganReport(),
                            mUserRef = userRef, mFeedback = etOtherReport.trimmedText)
                }
                R.id.rbReportPretend -> {
                    reportUserProfile(mUserReportType = ValueMapping.onPretendReport(),
                            mUserRef = userRef, mFeedback = etOtherReport.trimmedText)
                }
                R.id.rbReportContent -> {
                    reportUserProfile(mUserReportType = ValueMapping.onContentReport(),
                            mUserRef = userRef, mFeedback = etOtherReport.trimmedText)
                }
                R.id.rbReportOther -> {
                    if (!etOtherReport.verifyTextForErrors()) {
                        etOtherReport.showError(resId = R.string.write_report_reason)
                        return@setOnClickListener

                    } else {
                        reportUserProfile(mUserReportType = ValueMapping.onOtherReport(),
                                mUserRef = userRef, mFeedback = etOtherReport.trimmedText)
                    }
                }
                else -> {
                    showMessage(resId = R.string.select_report_reason_message)
                    return@setOnClickListener
                }
            }
        }
        // Toggle Visibility of  TextInput Layout in case if any other option is selected and clear its text.
        rbContainer.setOnCheckedChangeListener { _, checkedRadioButtonId ->
            when (checkedRadioButtonId) {
                R.id.rbReportNonVegan -> {
                    tilOtherReview.gone()
                    etOtherReport.setText("")
                }
                R.id.rbReportPretend -> {
                    tilOtherReview.gone()
                    etOtherReport.setText("")
                }
                R.id.rbReportContent -> {
                    tilOtherReview.gone()
                    etOtherReport.setText("")
                }
                R.id.rbReportOther -> {
                    tilOtherReview.visible()
                }
            }
        }
    }

    private fun observeProperties() {
        mReportProfileViewModel.onSuccessfulProfileReported().observe(viewLifecycleOwner, { reported ->
            if (reported) {
                performTransaction(ReportSentFragment.newInstance(true), ReportSentFragment.TAG)
            }
        })
    }

    private fun reportUserProfile(
            mShowLoader: Boolean = true, mUserReportType: Int,
            mUserRef: String, mFeedback: String,
    ) {
        mReportProfileViewModel.reportProfile(mShowLoader, mUserReportType, mUserRef, mFeedback)
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }
}