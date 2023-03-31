package com.forthgreen.app.views.fragments

import android.os.Bundle
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Tab
import com.forthgreen.app.viewmodels.BaseViewModel
import kotlinx.android.synthetic.main.fragment_continue_with_email_tab.*

/**
 * @author Nandita Gandhi
 * @since 22 Mar 2021
 */
class ContinueWithEmailTabFragment : BaseTabLayoutFragment() {

    companion object {
        const val TAG = "ContinueWithEmailTabFragment"
        private const val BUNDLE_EXTRAS_IS_SIGN_IN = "BUNDLE_EXTRAS_IS_SIGN_IN"
        fun newInstance(isSignIn: Boolean): ContinueWithEmailTabFragment {
            return ContinueWithEmailTabFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(BUNDLE_EXTRAS_IS_SIGN_IN, isSignIn)
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_continue_with_email_tab

    override val viewModel: BaseViewModel?
        get() = null

    // Variables
    private val isSignIn by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRAS_IS_SIGN_IN)
    }

    override fun initTabs() {
        setupViews()
    }

    private fun setupViews() {
        val listOfTabs = ArrayList<Tab>()

        listOfTabs.add(Tab(SignUpFragment(), getString(R.string.sign_up_label), null, true))
        listOfTabs.add(Tab(LoginFragment(), getString(R.string.login), null, true))

        setViewPager(getString(R.string.continue_with_email_label), listOfTabs)

        if (isSignIn) {
            viewPager.currentItem = 1
        } else {
            viewPager.currentItem = 0
        }
    }
}