package com.forthgreen.app.views.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.RateAndReviewViewModel
import com.forthgreen.app.views.adapters.MyReviewsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Chetan Tuteja on 16-May-20.
 */
class MyReviewsFragment : BaseRecyclerViewFragment(), LoadMoreListener {

    companion object {
        const val TAG = "MyReviewsFragment"
    }

    //Variables
    private var mPage = 1
    private val resultSize = 30
    private val mAdapter by lazy {
        MyReviewsAdapter(this)
    }
    private val mRateAndReview: RateAndReviewViewModel by lazy {
        //getting viewModel
        ViewModelProvider(this).get(RateAndReviewViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_my_reviews

    override val viewModel: BaseViewModel?
        get() = mRateAndReview

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        setupViews()
        observeProperties()
    }

    private fun setupViews() {
        //Setup toolbar title
        tvToolbarTitle.text = getString(R.string.my_reviews_toolbar_title)
        mRateAndReview.getMyReviews(true, mPage, resultSize)
    }

    private fun observeProperties() {
        mRateAndReview.onSuccess().observe(viewLifecycleOwner, Observer {
            mAdapter.myReviews(it.data, mPage, it.hasMore)
            if (mPage == 1 && it.data.isEmpty())
                showNoDataText(resId = null, message = "You haven’t reviewed any products yet.")
            else {
                hideNoDataText()
            }
        })

    }

    override fun onPullDownToRefresh() {
        mPage = 1
        mRateAndReview.getMyReviews(false, mPage, resultSize)
    }

    override fun onLoadMore() {
        mPage++
        mRateAndReview.getMyReviews(false, mPage, resultSize)
    }
}
