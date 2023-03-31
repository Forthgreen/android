package com.forthgreen.app.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ProductsFiltersViewModel
import com.forthgreen.app.views.adapters.HomeFilterAdapter
import com.forthgreen.app.views.utils.supportFragmentManager
import kotlinx.android.synthetic.main.fragment_filter_options.*

class FilterOptionsFragment : BaseRecyclerViewFragment(), HomeFilterAdapter.HomeFilterCallbacks {

    companion object {
        const val TAG = "FilterOptionsFragment"
        const val BUNDLE_EXTRAS_FILTER_LIST = "BUNDLE_EXTRAS_FILTER_LIST"
        const val LOCAL_INTENT_FILTERS_UPDATED = "LOCAL_INTENT_FILTERS_UPDATED"
        const val BUNDLE_EXTRAS_GENDER_TAB = "BUNDLE_EXTRAS_GENDER_TAB"

        fun newInstance(filterList: ArrayList<Filter>, womenTab: Boolean): FilterOptionsFragment {
            return FilterOptionsFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(BUNDLE_EXTRAS_FILTER_LIST, filterList)
                    putBoolean(BUNDLE_EXTRAS_GENDER_TAB, womenTab)
                }
            }
        }
    }

    // Variables
    private val mAdapter by lazy { HomeFilterAdapter(this) }

    private val filters by lazy {
        requireArguments().getParcelableArrayList<Filter>(BUNDLE_EXTRAS_FILTER_LIST)!!
    }
    private val mFiltersViewModel by lazy {
        ViewModelProvider(requireParentFragment()).get(ProductsFiltersViewModel::class.java)
    }
    private val mWomenTab by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRAS_GENDER_TAB, false)
    }

    override val layoutId: Int
        get() = R.layout.fragment_filter_options

    override val viewModel: BaseViewModel?
        get() = mFiltersViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Send the list to adapter
        mAdapter.updateList(filters)
    }

    private fun setupListeners() {
        btnProducts.setOnClickListener {
            val updatedFilterList = mAdapter.getFilterList()
            val gender = if (mWomenTab) {
                ValueMapping.onWomenFilters()
            } else {
                ValueMapping.onMaleFilters()
            }
            // Send broadcast
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                    Intent(LOCAL_INTENT_FILTERS_UPDATED).apply {
                        putExtra(BUNDLE_EXTRAS_FILTER_LIST, updatedFilterList)
                        putExtra(BUNDLE_EXTRAS_GENDER_TAB, gender)
                    })

            supportFragmentManager().popBackStack()
        }
    }

    private fun observeProperties() {
        mFiltersViewModel.onSelectAllFilters().observe(viewLifecycleOwner, { select ->
            mAdapter.isSelectAll(select)
        })
    }

    override fun onPullDownToRefresh() {
    }

    override fun onFilterChange(filterList: List<Filter>) {
        mFiltersViewModel.checkSelectedFilters(filterList)
    }
}