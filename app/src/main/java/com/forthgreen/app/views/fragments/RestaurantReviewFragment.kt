package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.RestaurantReviewViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_restaurant_review.*
import kotlinx.android.synthetic.main.toolbar.*

class RestaurantReviewFragment : BaseFragment() {

    companion object {
        const val TAG = "RestaurantReviewFragment"

        private const val BUNDLE_EXTRAS_REF = "RefForTheSelectedProduct"

        fun newInstance(mRef: String = ""): RestaurantReviewFragment {
            return RestaurantReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_REF, mRef)
                }
            }
        }
    }

    //variables
    //variables
    private val restaurantRef by lazy { arguments?.getString(BUNDLE_EXTRAS_REF, "")!! }
    private val mRestaurantReviewViewModel by lazy {
        //getting viewModel
        ViewModelProvider(this).get(RestaurantReviewViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_restaurant_review

    override val viewModel: BaseViewModel?
        get() = mRestaurantReviewViewModel

    override fun init() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        tvToolbarTitle.text = getString(R.string.review_label)
        PushDownAnim.setPushDownAnimTo(btnSubmit, rbRating)
        // Toolbar back cross btn specific for this screen
        toolbar.setNavigationIcon(R.drawable.ic_close)
    }

    private fun setupListeners() {
        btnSubmit.setOnClickListener {
            verifyDetails()
        }
    }

    //Helper method to validate details.
    private fun verifyDetails() {
        val title = etReviewTitle.text.toString().trim()
        val review = etRestaurantReview.text.toString().trim()

        if (!etReviewTitle.verifyTextForErrors()) {
            etReviewTitle.error = getString(R.string.empty_field_error)
            etReviewTitle.requestFocus()
            return
        }

        if (!etRestaurantReview.verifyTextForErrors()) {
            etRestaurantReview.error = getString(R.string.empty_field_error)
            etRestaurantReview.requestFocus()
            return
        }

        if (rbRating.rating.toDouble() == 0.0) {
            showMessage(R.string.no_rating_error)
            return
        }

        mRestaurantReviewViewModel.addRestaurantReview(restaurantRef, rbRating.rating, title, review)
    }

    private fun observeProperties() {
        mRestaurantReviewViewModel.onSuccessfulAddedReview().observe(viewLifecycleOwner, Observer { addReview ->
            if (addReview.message == "Success") {
                performTransaction(RestaurantReviewSentFragment(), RestaurantReviewSentFragment.TAG)
            } else {
                showMessage(message = addReview.message)
            }
        })
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }
}