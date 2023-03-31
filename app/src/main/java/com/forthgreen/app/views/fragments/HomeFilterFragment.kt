package com.forthgreen.app.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.adapters.HomeFilterAdapter
import com.forthgreen.app.views.utils.supportFragmentManager
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_home_filter.*

class HomeFilterFragment : BaseRecyclerViewFragment(), HomeFilterAdapter.HomeFilterCallbacks {

    companion object {
        const val TAG = "HomeFilterFragment"
        const val LOCAL_INTENT_SHOP_FILTER_UPDATED = "LOCAL_INTENT_SHOP_FILTER_UPDATED"
        const val LOCAL_INTENT_RESTAURANT_FILTER_UPDATED = "LOCAL_INTENT_RESTAURANT_FILTER_UPDATED"
        const val BUNDLE_EXTRAS_FILTER_LIST = "BUNDLE_EXTRAS_FILTER_LIST"
        const val BUNDLE_EXTRA_FILTER_TYPE = "BUNDLE_EXTRA_FILTER_TYPE"

        fun newInstance(filterList: ArrayList<Filter>, restaurantFilters: Boolean = false): HomeFilterFragment {
            return HomeFilterFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(BUNDLE_EXTRAS_FILTER_LIST, filterList)
                    putBoolean(BUNDLE_EXTRA_FILTER_TYPE, restaurantFilters)
                }
            }
        }
    }

    // Variables
    private var isAllSelected = false

    private val filterAdapter by lazy {
        HomeFilterAdapter(this)
    }
    private val filterList by lazy {
        requireArguments().getParcelableArrayList<Filter>(BUNDLE_EXTRAS_FILTER_LIST)!!
    }
    private val restaurantFilters by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRA_FILTER_TYPE, false)
    }

    override val layoutId: Int
        get() = R.layout.fragment_home_filter

    override val viewModel: BaseViewModel?
        get() = null

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = filterAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        // hide toolbar
        toolbar.setNavigationIcon(R.drawable.ic_cancel_dialog)
        setupViews()
        setUpListeners()
    }

    private fun setupViews() {
        // Set Toolbar and button text
        if (restaurantFilters) {
            tvToolbarTitle.text = getString(R.string.frag_filter_title)
            btnProducts.text = getString(R.string.btn_view_restaurants)
        } else {
            tvToolbarTitle.text = getString(R.string.filter_toolbar_title)
            btnProducts.text = getString(R.string.btn_view_products)
        }

        // Set Push down Anim
        PushDownAnim.setPushDownAnimTo(btnProducts, tvClearFilter)

        // Set data to filter adapter
        filterAdapter.updateList(filterList)

        // Toolbar filter clear/ add all
        onFilterChange(filterList)
    }

    private fun setUpListeners() {
        btnProducts.setOnClickListener {
            // Send Broadcast for the updated Profession
            val updatedFilterList = filterAdapter.getFilterList()
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                    Intent().apply {
                        action = if (restaurantFilters) {
                            LOCAL_INTENT_RESTAURANT_FILTER_UPDATED
                        } else {
                            LOCAL_INTENT_SHOP_FILTER_UPDATED
                        }
                        putExtra(BUNDLE_EXTRAS_FILTER_LIST, updatedFilterList)
                    }
            )
            supportFragmentManager().popBackStack()
        }
        tvClearFilter.setOnClickListener {
            if (isAllSelected) {
                isAllSelected = false
                filterAdapter.isSelectAll(false)
                tvClearFilter.text = getString(R.string.all_label)
            } else {
                isAllSelected = true
                filterAdapter.isSelectAll(true)
                tvClearFilter.text = getString(R.string.clear_filter_label)
            }
        }
    }

    override fun onPullDownToRefresh() {
    }

    override fun onFilterChange(filterList: List<Filter>) {
        if (filterList.all { it.isSelected }) {
            tvClearFilter.text = getString(R.string.clear_filter_label)
            isAllSelected = true
        } else {
            tvClearFilter.text = getString(R.string.all_label)
            isAllSelected = false
        }
    }
}