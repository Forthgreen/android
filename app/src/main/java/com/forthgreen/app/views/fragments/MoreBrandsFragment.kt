package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.HomeViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.MoreBrandsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.fragment_more_brands.*
import kotlinx.android.synthetic.main.toolbar.*

class MoreBrandsFragment : BaseRecyclerViewFragment(), MoreBrandsAdapter.ShowBrandDetail, LoadMoreListener {
    companion object {
        const val TAG = "ViewMoreBrands"
    }

    //variables
    private var mPage = 1
    private val resultSize = 30
    private val moreBrandsAdapter: MoreBrandsAdapter by lazy {
        MoreBrandsAdapter(this, this)
    }

    private val mHomeViewModel: HomeViewModel by lazy {
        //getting viewModel
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private var filterList = arrayListOf<Filter>()

    override val layoutId: Int
        get() = R.layout.fragment_more_brands

    override val viewModel: BaseViewModel?
        get() = mHomeViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = moreBrandsAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false


    override fun setData() {
        // Populate filter list
        filterList.addAll(Constants.categoriesList)

        tvToolbarTitle.text = getString(R.string.tool_title)
        //Set toolbar title as drawable
        toolbar.setNavigationIcon(R.drawable.ic_preferences)
        ivToolbarActionEnd.setImageResource(R.drawable.ic_search_icon)
        ivToolbarActionEnd.visible()

        mHomeViewModel.getBrandList(true, getFilterIds(), mPage, resultSize)
        setUpListeners()
        observeProperties()
    }

    private fun setUpListeners() {
        toolbar.setNavigationOnClickListener {
            //show_preferences
            performTransaction(HomeFilterFragment.newInstance(filterList, false), HomeFilterFragment.TAG)
        }
        ivToolbarActionEnd.setOnClickListener {
            //search_results_of_brands_and_products
            performTransaction(HomeSearchFragment(), HomeSearchFragment.TAG)
        }

        //Register Receiver
      //  mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, IntentFilter(HomeFilterFragment.LOCAL_INTENT_BRAND_FILTER_UPDATED))
    }

    private fun observeProperties() {
        mHomeViewModel.onReceivingBrands().observe(viewLifecycleOwner, Observer {
            //Hide Shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()
            moreBrandsAdapter.getMoreBrands(it.data, mPage, it.hasMore)
        })
    }

    override fun showBrandDetail(brands: Brand) {
        performTransaction(BrandDetailFragment.newInstance(brands), BrandDetailFragment.TAG)
    }

    fun scrollToTop() {
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
    }

    override fun onLoadMore() {
        mPage++
        mHomeViewModel.getBrandList(false, getFilterIds(), mPage, resultSize)
    }

    override fun onPullDownToRefresh() {
        //Show Shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()
        mPage = 1
        mHomeViewModel.getBrandList(false, getFilterIds(), mPage, resultSize)
    }

    private fun getFilterIds(): List<Int> {
        val filterIds = mutableListOf<Int>()
        filterList.forEach {
            if (it.isSelected)
                filterIds.add(it.id)
        }
        return filterIds.toList()
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = false)
    }

    //Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                if (intent.hasExtra(HomeFilterFragment.BUNDLE_EXTRAS_FILTER_LIST)) {
                    val updatedFilterList = intent.getParcelableArrayListExtra<Filter>(HomeFilterFragment.BUNDLE_EXTRAS_FILTER_LIST)!!
                    filterList = updatedFilterList
                    mPage = 1
                    //Show Shimmer
                    flShimmer.startShimmer()
                    flShimmer.visible()
                    swipeRefreshLayout.gone()
                    mHomeViewModel.getBrandList(false, getFilterIds(), mPage, CategoryProductsFragment.PRODUCT_RESULTS_PER_PAGE)
                }
            }
        }
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }

}