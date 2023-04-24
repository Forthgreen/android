package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.MyBrandsViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.MyBrandsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.fragment_my_brands.*
import kotlinx.android.synthetic.main.toolbar.*

class MyBrandsFragment : BaseRecyclerViewFragment(), LoadMoreListener {

    companion object {
        const val TAG = "MyBrandsFragment"
    }

    //variables
    private var mPage = 1

    private val resultSize = 30

    private val mBrandsViewModel: MyBrandsViewModel by lazy {
        ViewModelProvider(this).get(MyBrandsViewModel::class.java)
    }

    private val mBrandsAdapter by lazy { MyBrandsAdapter(this) }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_my_brands

    override val viewModel: BaseViewModel?
        get() = mBrandsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mBrandsAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true


    override fun setData() {
        toolbar.gone()

        //Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(BrandDetailFragment.LOCAL_INTENT_BRAND_FOLLOWED)
                    addAction(BrandDetailFragment.LOCAL_INTENT_BRAND_BOOKMARKED)
                    addAction(MyStuffFragment.LOCAL_INTENT_SHOW_SHIMMER)
                }
        )

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mBrandsViewModel.getBookmarkedBrands(
                true,
                mPage,
                resultSize,
                ValueMapping.onBrandBookmarkAction()
            )
        }

        observeProperties()
    }

    private fun observeProperties() {
        mBrandsViewModel.onReceivingMyBrands().observe(viewLifecycleOwner, Observer { brands ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()

            if (mPage == 1 && brands.data.isEmpty()) {
                mBrandsAdapter.updateList(emptyList(), mPage, false)
                showNoDataText(R.string.no_brands_followed_message)
            } else {
                hideNoDataText()
                mBrandsAdapter.updateList(brands.data, mPage, brands.hasMore)
            }
        })

    }

    override fun onPullDownToRefresh() {
        mPage = 1
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mBrandsViewModel.getBookmarkedBrands(
                true,
                mPage,
                resultSize,
                ValueMapping.onBrandBookmarkAction()
            )
        }
    }

    override fun onLoadMore() {
        mPage++
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mBrandsViewModel.getBookmarkedBrands(
                true,
                mPage,
                resultSize,
                ValueMapping.onBrandBookmarkAction()
            )
        }

    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = false)
    }

    //Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                if (intent.action == MyStuffFragment.LOCAL_INTENT_SHOW_SHIMMER) {
                    showShimmer()
                } else {
                    onPullDownToRefresh()
                }
            }
        }
    }

    private fun showShimmer() {
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        //get brands
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mBrandsViewModel.getBookmarkedBrands(
                true,
                mPage,
                resultSize,
                ValueMapping.onBrandBookmarkAction()
            )
        }
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}