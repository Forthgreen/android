package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ProductReviewViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_product_review.*
import kotlinx.android.synthetic.main.toolbar.*

class ProductReviewFragment : BaseFragment() {

    companion object {
        const val TAG = "ProductReviewFragment"
        private const val BUNDLE_EXTRAS_PRODUCT_REF = "RefForTheSelectedProduct"

        fun newInstance(mProductRef: String = ""): ProductReviewFragment {
            return ProductReviewFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_PRODUCT_REF, mProductRef)
                }
            }
        }
    }

    //variables
    private val productRef by lazy { arguments?.getString(BUNDLE_EXTRAS_PRODUCT_REF, "")!! }
    private val mReviewViewModel: ProductReviewViewModel by lazy {
        ViewModelProvider(this).get(ProductReviewViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_product_review

    override val viewModel: BaseViewModel?
        get() = mReviewViewModel

    override fun init() {
        setUpReviews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpReviews() {
        tvToolbarTitle.text = getString(R.string.review_label)
        PushDownAnim.setPushDownAnimTo(btnSubmitReview, rbProductRating)
        // Toolbar back cross btn specific for this screen
        toolbar.setNavigationIcon(R.drawable.ic_close)
    }

    private fun setUpListeners() {
        btnSubmitReview.setOnClickListener {
            verifyDetails()
        }
    }

    //Helper method to validate details.
    private fun verifyDetails() {
        val title = etTitle.text.toString().trim()
        val review = etReview.text.toString().trim()

        if (!etTitle.verifyTextForErrors()) {
            etTitle.error = getString(R.string.empty_field_error)
            etTitle.requestFocus()
            return
        }

        if (!etReview.verifyTextForErrors()) {
            etReview.error = getString(R.string.empty_field_error)
            etReview.requestFocus()
            return
        }

        if (rbProductRating.rating.toDouble() == 0.0) {
            showMessage(R.string.no_rating_error)
            return
        }

        mReviewViewModel.addReview(productRef, rbProductRating.rating, title, review)
    }

    private fun observeProperties() {
        mReviewViewModel.onSuccessfulAdd().observe(viewLifecycleOwner, Observer { reviewSuccess ->
            if (reviewSuccess) {
                performTransaction(ProductReviewSentFragment(), ProductReviewSentFragment.TAG)
            }
        })
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }
}