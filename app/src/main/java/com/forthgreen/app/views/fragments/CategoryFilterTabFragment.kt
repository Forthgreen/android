package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.repository.models.Tab
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ProductsFiltersViewModel
import kotlinx.android.synthetic.main.fragment_category_filter_tab.*

/**
 * @author Nandita Gandhi
 * @since 05-08-2021
 */
class CategoryFilterTabFragment : BaseTabLayoutFragment() {

    companion object {
        const val TAG = "CategoryFilterFragment"
        private const val BUNDLE_EXTRAS_WOMEN_FILTERS = "BUNDLE_EXTRAS_WOMEN_FILTERS"
        private const val BUNDLE_EXTRAS_MEN_FILTERS = "BUNDLE_EXTRAS_MEN_FILTERS"

        fun newInstance(womenFilters: ArrayList<Filter>, menFilters: ArrayList<Filter>): CategoryFilterTabFragment {
            return CategoryFilterTabFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(BUNDLE_EXTRAS_WOMEN_FILTERS, womenFilters)
                    putParcelableArrayList(BUNDLE_EXTRAS_MEN_FILTERS, menFilters)
                }
            }
        }
    }

    // Variables
    private var isAllSelected = false

    private val womanFilterList by lazy {
        requireArguments().getParcelableArrayList<Filter>(BUNDLE_EXTRAS_WOMEN_FILTERS)!!
    }
    private val menFilterList by lazy {
        requireArguments().getParcelableArrayList<Filter>(BUNDLE_EXTRAS_MEN_FILTERS)!!
    }
    private val mFiltersViewModel by lazy {
        ViewModelProvider(this).get(ProductsFiltersViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_category_filter_tab

    override val viewModel: BaseViewModel?
        get() = mFiltersViewModel

    override fun initTabs() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        val listOfTabs = ArrayList<Tab>()

        listOfTabs.add(Tab(FilterOptionsFragment.newInstance(womanFilterList, true), getString(R.string.woman_label), null, true))
        listOfTabs.add(Tab(FilterOptionsFragment.newInstance(menFilterList, false), getString(R.string.men_label), null, true))

        setViewPager(getString(R.string.filter_toolbar_title), listOfTabs)

        // Setup toolbar
        toolbar.setNavigationIcon(R.drawable.ic_cancel_dialog)

        if (viewPager.currentItem == 0) {
            mFiltersViewModel.checkSelectedFilters(womanFilterList)
        } else {
            mFiltersViewModel.checkSelectedFilters(menFilterList)
        }
    }

    private fun setupListeners() {
        tvClearFilter.setOnClickListener {
            if (isAllSelected) {
                isAllSelected = false
                tvClearFilter.text = getString(R.string.all_label)
            } else {
                isAllSelected = true
                tvClearFilter.text = getString(R.string.clear_filter_label)
            }
            mFiltersViewModel.selectAllFilters(isAllSelected)
        }
    }

    private fun observeProperties() {
        mFiltersViewModel.onFilterChanged().observe(viewLifecycleOwner, { allSelected ->
            tvClearFilter.text = if (allSelected) {
                getString(R.string.clear_filter_label)
            } else {
                getString(R.string.all_label)
            }
            isAllSelected = allSelected
        })
    }
}