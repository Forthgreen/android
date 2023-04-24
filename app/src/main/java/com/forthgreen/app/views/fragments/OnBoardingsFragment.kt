package com.forthgreen.app.views.fragments

import com.forthgreen.app.R
import com.forthgreen.app.repository.models.OnBoardings
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.adapters.OnBoardingsAdapter
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_onboadings.*

class OnBoardingsFragment : BaseFragment() {

    companion object {
        const val TAG = "OnBoardingsFragment"
    }

    // Variables
    private val mAdapter by lazy { OnBoardingsAdapter() }

    override val layoutId: Int
        get() = R.layout.fragment_onboadings

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        setupViews()
        setUpListeners()
    }

    private fun setupViews() {
        // Push down anim
        PushDownAnim.setPushDownAnimTo(btnNext, tvSkipLabel)

        // Assign adapter to viewPager and viewPager to dots indicator
        viewPager.adapter = mAdapter
        dotIndicator.setViewPager2(viewPager)

        // Send onBoarding data to adapter
        mAdapter.submitList(listOf(OnBoardings(title = R.string.boarding_title_one_label, description = R.string.boarding_desc_one, boardingImage = R.drawable.ic_onboarding_one),
                OnBoardings(title = R.string.boarding_title_two_label, description = R.string.boarding_desc_two, boardingImage = R.drawable.ic_onboarding_two),
                OnBoardings(title = R.string.boarding_title_three_label, description = R.string.boarding_desc_three, boardingImage = R.drawable.ic_onboarding_three),
                OnBoardings(title = R.string.boarding_title_four_label, description = R.string.boarding_desc_four, boardingImage = R.drawable.ic_onboarding_four)
        ))
    }

    private fun setUpListeners() {
        btnNext.setOnClickListener {
            when (viewPager.currentItem) {
                3 -> {
                   // performFragTransaction(WelcomeFragment.newInstance(isFromMyBrands = false), WelcomeFragment.TAG)
                    performFragTransaction(WelcomeFragment.newInstance(isFromMyBrands = true), WelcomeFragment.TAG)
                }
                else -> {
                    viewPager.currentItem = viewPager.currentItem + 1
                }
            }
        }
        tvSkipLabel.setOnClickListener {
            performFragTransaction(WelcomeFragment.newInstance(isFromMyBrands = true), WelcomeFragment.TAG)
           // performFragTransaction(WelcomeFragment.newInstance(isFromMyBrands = false), WelcomeFragment.TAG)
        }
    }
}