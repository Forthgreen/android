package com.forthgreen.app.views.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.HomeSearchViewModel
import com.forthgreen.app.views.adapters.HomeSearchAdapter
import com.forthgreen.app.views.utils.doOnTextChanged
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_home_search.*

class HomeSearchFragment : BaseRecyclerViewFragment() {

    companion object {
        const val TAG = "Search Brands and Products"
        private const val mResultSize = 50
    }

    //Variables
    private val searchAdapter: HomeSearchAdapter by lazy { HomeSearchAdapter(this) }

    private val mHomeSearchViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(HomeSearchViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_home_search

    override val viewModel: BaseViewModel?
        get() = mHomeSearchViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = searchAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        setUpViews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpViews() {
        //Set PushDown Anim
        PushDownAnim.setPushDownAnimTo(tvCancelSearch)

        // Hit the API for all products, brands
        mHomeSearchViewModel.searchBrandProducts(mResultSize)

        etSearchProductsOrBrands.requestFocus()
        GeneralFunctions.showKeyboard(requireContext())
    }

    private fun setUpListeners() {
        tvCancelSearch.setOnClickListener {
            GeneralFunctions.hideKeyboard(this)
            supportFragmentManager().popBackStack()
        }
        etSearchProductsOrBrands.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                // Hit the Search API with the query performed.
                mHomeSearchViewModel.searchBrandProducts(mResultSize, text)
                // Show Shimmer
                flShimmer.startShimmer()
                flShimmer.visible()
                recyclerView.gone()
            } else {
                searchAdapter.submitList(emptyList())
            }
        }
    }

    //Helper method to observe data via ViewModel
    private fun observeProperties() {
        mHomeSearchViewModel.onSuccessfulBrandProductsFetched().observe(viewLifecycleOwner, Observer { brandProducts ->
            //Hide Shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            recyclerView.visible()
            searchAdapter.submitList(brandProducts.data.list)
            if (brandProducts.data.list.isEmpty()) {
                //If the list received via API is empty, submit an empty list instead.
                showNoDataText(R.string.no_query_result)
            } else {
                hideNoDataText()
            }
        })
    }

    override fun onPullDownToRefresh() {
        //nothing
    }
}
