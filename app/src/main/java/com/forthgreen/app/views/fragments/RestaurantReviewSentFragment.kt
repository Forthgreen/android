package com.forthgreen.app.views.fragments

import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.utils.supportFragmentManager
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_restaurant_review_sent.*

class RestaurantReviewSentFragment : BaseFragment() {
    companion object {
        const val TAG = "ProductReviewSentFragment"
    }

    override val layoutId: Int
        get() = R.layout.fragment_restaurant_review_sent

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        setUpViews()
        setUpListeners()
    }

    private fun setUpViews() {
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