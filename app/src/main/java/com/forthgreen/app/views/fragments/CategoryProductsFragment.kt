package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.HomeViewModel
import com.forthgreen.app.views.adapters.ProductListAdapter
import com.forthgreen.app.views.adapters.ProductListIemDecoration
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.bottom_sheet_product_sort_options.*
import kotlinx.android.synthetic.main.fragment_category_products.*
import kotlinx.android.synthetic.main.toolbar.*


class CategoryProductsFragment : BaseRecyclerViewFragment(),
        ProductListAdapter.ProductDetailCallback, LoadMoreListener {

    companion object {
        const val TAG = "CategoryProductsFragment"
        const val PRODUCT_RESULTS_PER_PAGE = 30
        private const val BUNDLE_EXTRAS_CATEGORY = "BUNDLE_EXTRAS_CATEGORY"
        const val DEFAULT_SORT_VALUE = 0

        fun newInstance(category: Filter): CategoryProductsFragment {
            return CategoryProductsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRAS_CATEGORY, category)
                }
            }
        }
    }

    // Variables
    private var mPage = 1

    private var bookmarkProductStatus = Product()

    private val mCategoryProductsViewModel: HomeViewModel by lazy {
        // Getting viewModel
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val productsAdapter by lazy {
        // Adapter for products
        ProductListAdapter(this, true)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }
    private var filterList = arrayListOf<Filter>()

    private var genderValue = ValueMapping.onBothGenderFilters()

    private val mCategory by lazy {
        requireArguments().getParcelable<Filter>(BUNDLE_EXTRAS_CATEGORY)!!
    }
    private var sort = DEFAULT_SORT_VALUE

    private lateinit var mGridlayoutManager: GridLayoutManager

    override val layoutId: Int
        get() = R.layout.fragment_category_products

    override val viewModel: BaseViewModel?
        get() = mCategoryProductsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = productsAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = mGridlayoutManager


    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setUpViews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpViews() {
        // Set toolbar title
        tvToolbarTitle.text = mCategory.name

        // set elevation
        toolbar.elevation = 0f

        // Push down anim
        PushDownAnim.setPushDownAnimTo(tvSortLabel, tvFilterLabel)

        // Initialise layout manager
        mGridlayoutManager = GridLayoutManager(activity, 2)
        // Return span of 2 in case of loadMore so that it can occupy whole space instead of half.
        mGridlayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (productsAdapter.getItemViewType(position)) {
                    ProductListAdapter.ROW_TYPE_LOAD_MORE -> 2
                    else -> 1
                }
            }
        }

        recyclerView.apply {
            val productSpacing = resources.getDimensionPixelSize(R.dimen.home_product_offset)
            addItemDecoration(ProductListIemDecoration(productSpacing, 2))
        }

        // Fetch products according to category
        performAPICall(true)

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(HomeFilterFragment.LOCAL_INTENT_SHOP_FILTER_UPDATED)
                    addAction(FilterOptionsFragment.LOCAL_INTENT_FILTERS_UPDATED)
                    addAction(ProductDetailFragment.BOOKMARK_INTENT_STATUS_UPDATED)
                })
    }

    private fun setUpListeners() {
        tvSortLabel.setOnClickListener {
            // Show sort options bottom sheet
            sortOptionsBottomSheet.show()
        }
        tvFilterLabel.setOnClickListener {
            // Open Filter options
            openFiltersOptions()
        }
    }

    private fun observeProperties() {
        // Observe received data
        mCategoryProductsViewModel.onReceivingProducts().observe(viewLifecycleOwner, Observer {
            // Hide Shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            groupFilterSort.visible()
            swipeRefreshLayout.visible()
            productsAdapter.updateList(it.data, mPage, it.hasMore)

            if (mPage == 1 && it.data.isEmpty()) {
                tvNoData.visible()
                tvNoData.text = getString(R.string.no_products_found_message)
            } else {
                tvNoData.gone()
            }
        })
        mCategoryProductsViewModel.onBookmarkAddSuccess().observe(viewLifecycleOwner, { success ->
            if (success) {
                productsAdapter.updateProduct(bookmarkProductStatus.copy(isBookmark = !bookmarkProductStatus.isBookmark))
                LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(Intent(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED))
            }
        })
    }

    override fun showProductDetail(product: Product) {
        performFragTransaction(
                ProductDetailFragment.newInstance(product), ProductDetailFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
    }

    override fun updateFollowStatus(product: Product) {
        // Vibrate phone
        GeneralFunctions.vibratePhone(requireContext())
        //Show Dialog in Case of Guest User else perform action.
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            showGuestModeDialog()
        } else {
            bookmarkProductStatus = product
            mCategoryProductsViewModel.addBookmark(
                    true,
                    product._id,
                    ValueMapping.onProductBookmarkAction(),
                    !product.isBookmark
            )
        }
    }

    //Show Guest Mode Dialog with help of Material Dialog by inflating custom layout.
    private fun showGuestModeDialog() {
      //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
        callUserLoginDialog()
    }

    private fun callUserLoginDialog() {
        val userLoginDialog = UserLoginDialog()
        userLoginDialog.showUserLoginDialog(requireActivity() as AppCompatActivity, object :
            LoginButtonClickInterface {
            override fun loginButtonClick() {
                performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            }
        })
    }

    private fun openFiltersOptions() {
        when (mCategory.id) {
            ValueMapping.onClothingCategorySelected() -> {
                performFragTransaction(
                        CategoryFilterTabFragment.newInstance(
                                Constants.clothingWomenFilterList,
                                Constants.clothingMenFilterList
                        ), CategoryFilterTabFragment.TAG,
                        enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
            ValueMapping.onBeautyCategorySelected() -> {
                performFragTransaction(
                        CategoryFilterTabFragment.newInstance(
                                Constants.beautyWomenFilterList,
                                Constants.beautyMenFilterList
                        ), CategoryFilterTabFragment.TAG,
                        enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
            ValueMapping.onAccessoriesCategorySelected() -> {
                performFragTransaction(
                        CategoryFilterTabFragment.newInstance(
                                Constants.accessoriesWomenFilterList,
                                Constants.accessoriesMenFilterList
                        ), CategoryFilterTabFragment.TAG,
                        enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
            ValueMapping.onHealthCategorySelected() -> {
                performFragTransaction(
                        HomeFilterFragment.newInstance(Constants.healthFilterList),
                        HomeFilterFragment.TAG, enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
            ValueMapping.onFoodCategorySelected() -> {
                performFragTransaction(
                        HomeFilterFragment.newInstance(Constants.foodFilterList),
                        HomeFilterFragment.TAG, enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
            ValueMapping.onDrinksCategorySelected() -> {
                performFragTransaction(
                        HomeFilterFragment.newInstance(Constants.drinksFilterList),
                        HomeFilterFragment.TAG, enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
            ValueMapping.onMiscCategorySelected() -> {
                performFragTransaction(
                        HomeFilterFragment.newInstance(Constants.miscFilterList),
                        HomeFilterFragment.TAG, enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
        }
    }

    override fun onPullDownToRefresh() {
        mPage = 1
        sort = DEFAULT_SORT_VALUE
        filterList = arrayListOf()
        genderValue = ValueMapping.onBothGenderFilters()
        tvNoData.gone()
        performAPICall(mShowLoader = false, showShimmer = false)
    }

    override fun onLoadMore() {
        mPage++
        performAPICall(mShowLoader = false, showShimmer = false)
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                when (intent.action) {
                    FilterOptionsFragment.LOCAL_INTENT_FILTERS_UPDATED -> {
                        if (intent.hasExtra(FilterOptionsFragment.BUNDLE_EXTRAS_FILTER_LIST) &&
                                intent.hasExtra(FilterOptionsFragment.BUNDLE_EXTRAS_GENDER_TAB)
                        ) {
                            val updatedFilterList =
                                    intent.getParcelableArrayListExtra<Filter>(FilterOptionsFragment.BUNDLE_EXTRAS_FILTER_LIST)!!
                            val gender = intent.getIntExtra(
                                    FilterOptionsFragment.BUNDLE_EXTRAS_GENDER_TAB,
                                    ValueMapping.onBothGenderFilters()
                            )
                            filterList = updatedFilterList
                            genderValue = gender
                            mPage = 1
                            tvNoData.gone()
                            performAPICall(true)
                        }
                    }
                    HomeFilterFragment.LOCAL_INTENT_SHOP_FILTER_UPDATED -> {
                        if (intent.hasExtra(HomeFilterFragment.BUNDLE_EXTRAS_FILTER_LIST)) {
                            val updatedFilterList =
                                    intent.getParcelableArrayListExtra<Filter>(HomeFilterFragment.BUNDLE_EXTRAS_FILTER_LIST)!!
                            filterList = updatedFilterList
                            mPage = 1
                            tvNoData.gone()
                            performAPICall(true)
                        }
                    }
                    ProductDetailFragment.BOOKMARK_INTENT_STATUS_UPDATED -> {
                        onPullDownToRefresh()
                    }
                }
            }
        }
    }

    private fun getFilterIds(): List<Int> {
        val filterIds = mutableListOf<Int>()
        filterList.forEach {
            if (it.isSelected)
                filterIds.add(it.id)
        }
        return filterIds.toList()
    }

    private val sortOptionsBottomSheet
        get() = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).apply {
            customView(
                    viewRes = R.layout.bottom_sheet_product_sort_options,
                    noVerticalPadding = true
            )
            rbNewestFirst.setOnClickListener {
                sort = ValueMapping.onNewestFirstSort()
                performAPICall(true)
                this.dismiss()
            }
            rbPriceLToH.setOnClickListener {
                sort = ValueMapping.onPriceLowToHighSort()
                performAPICall(true)
                this.dismiss()
            }
            rbPriceHToL.setOnClickListener {
                sort = ValueMapping.onPriceHighToLowSort()
                performAPICall(true)
                this.dismiss()
            }
            cornerRadius(res = R.dimen.dialog_corner_radius)
        }

    private fun performAPICall(mShowLoader: Boolean, showShimmer: Boolean = true) {
        if (showShimmer) {
            // Start shimmer
            flShimmer.startShimmer()
            flShimmer.visible()
            groupFilterSort.gone()
            swipeRefreshLayout.gone()
        }
        // Hit the API
        mCategoryProductsViewModel.getProductList(
                isShowLoading = mShowLoader,
                categoryIds = listOf(mCategory.id), mPage = mPage,
                resultSize = PRODUCT_RESULTS_PER_PAGE, sort = sort, filter = getFilterIds(),
                mGender = genderValue
        )
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }
}