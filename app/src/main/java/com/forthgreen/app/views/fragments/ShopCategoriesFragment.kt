package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.repository.models.ProductCategory
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ShopCategoriesViewModel
import com.forthgreen.app.views.adapters.ShopCategoriesAdapter
import com.forthgreen.app.views.adapters.ShopProductsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.fragment_shop_categories.*
import kotlinx.android.synthetic.main.fragment_shop_categories.toolbar
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.toolbar

/**
 * @author Nandita Gandhi
 * @since 05-Aug-2021
 */
class ShopCategoriesFragment : BaseRecyclerViewFragment(), LoadMoreListener,
        ShopCategoriesAdapter.CategoryClickCallback,
        ShopProductsAdapter.ProductsCallback {

    companion object {
        const val TAG = "ShopCategoriesFragment"
        const val LOCAL_INTENT_PRODUCT_BOOKMARKED = "LOCAL_INTENT_PRODUCT_BOOKMARKED"
    }

    // Variables
    private val mAdapter by lazy { ShopCategoriesAdapter(this, this) }

    private var bookmarkProductStatus = Product()
    private var productCategory: Int = 0
    private val listOfProducts = mutableListOf<ProductCategory>()

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    private val mShopCategoriesViewModel by lazy {
        ViewModelProvider(this).get(ShopCategoriesViewModel::class.java)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_shop_categories

    override val viewModel: BaseViewModel?
        get() = mShopCategoriesViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = mLinearLayoutManager

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Toolbar setup
        tvToolbarTitle.text = getString(R.string.shop_toolbar_title)
        toolbar.toolbar.navigationIcon = null

        // Set up Linear Layout Manager
        mLinearLayoutManager = LinearLayoutManager(requireContext())

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        recyclerView.gone()

        // Hit API to fetch products of every category
        mShopCategoriesViewModel.fetchProductsCategory()

        // Receive broadcast
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(ProductDetailFragment.BOOKMARK_INTENT_STATUS_UPDATED)
                    addAction(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED)
                })
    }

    private fun setupListeners() {
        viewSearch.setOnClickListener {
            performFragTransaction(HomeSearchFragment(), HomeSearchFragment.TAG)
        }
    }

    private fun observeProperties() {
        val categoriesList = arrayListOf<Filter>()
        mShopCategoriesViewModel.onCategoryFetched().observe(viewLifecycleOwner, { productCategory ->
            flShimmer.stopShimmer()
            flShimmer.gone()
            recyclerView.visible()

            val clothingProds = productCategory.data.filter { category -> category._id == ValueMapping.onClothingCategorySelected() }
            val beautyProds = productCategory.data.filter { category -> category._id == ValueMapping.onBeautyCategorySelected() }
            val accessProds = productCategory.data.filter { category -> category._id == ValueMapping.onAccessoriesCategorySelected() }
            val foodProds = productCategory.data.filter { category -> category._id == ValueMapping.onFoodCategorySelected() }
            val drinksProds = productCategory.data.filter { category -> category._id == ValueMapping.onDrinksCategorySelected() }
            val healthProds = productCategory.data.filter { category -> category._id == ValueMapping.onHealthCategorySelected() }
            val miscProds = productCategory.data.filter { category -> category._id == ValueMapping.onMiscCategorySelected() }

            categoriesList.clear()
            categoriesList.add(Filter(1, "Clothing", products = clothingProds.first().products))
            categoriesList.add(Filter(2, "Beauty", products = beautyProds.first().products))
            categoriesList.add(Filter(7, "Accessories", products = accessProds.first().products))
            categoriesList.add(Filter(4, "Food", products = foodProds.first().products))
            categoriesList.add(Filter(5, "Drinks", products = drinksProds.first().products))
            categoriesList.add(Filter(3, "Health", products = healthProds.first().products))
            categoriesList.add(Filter(6, "Miscellaneous", products = miscProds.first().products))

            listOfProducts.clear()
            listOfProducts.addAll(productCategory.data)
            // Setup categories
            mAdapter.submitList(categoriesList, false)
        })

        mShopCategoriesViewModel.onBookmarkAddSuccess().observe(viewLifecycleOwner, { success ->
            if (success) {
                val productsList = mutableListOf<Product>()
                val category = listOfProducts.filter { category -> category._id == productCategory }
                productsList.addAll(category.first().products)
                val index = productsList.indexOfFirst { product -> product._id == bookmarkProductStatus._id }
                val product = productsList.find { product -> product._id == bookmarkProductStatus._id }
                product?.let { bookmarkProduct -> bookmarkProduct.isBookmark = !bookmarkProduct.isBookmark }
                when (productCategory) {
                    ValueMapping.onClothingCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(1, "Clothing", products = productsList), index)
                    }
                    ValueMapping.onBeautyCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(2, "Beauty", products = productsList), index)
                    }
                    ValueMapping.onAccessoriesCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(7, "Accessories", products = productsList), index)
                    }
                    ValueMapping.onFoodCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(4, "Food", products = productsList), index)
                    }
                    ValueMapping.onDrinksCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(5, "Drinks", products = productsList), index)
                    }
                    ValueMapping.onHealthCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(3, "Health", products = productsList), index)
                    }
                    ValueMapping.onMiscCategorySelected() -> {
                        mAdapter.updateProductBookmark(Filter(6, "Miscellaneous", products = productsList), index)
                    }
                }
                LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(Intent(LOCAL_INTENT_PRODUCT_BOOKMARKED))
            }
        })
    }

    fun scrollToTop() {
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        mLinearLayoutManager.startSmoothScroll(smoothScroller)
    }

    fun showShimmer() {
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        recyclerView.gone()

        // Hit API to fetch products of every category
        mShopCategoriesViewModel.fetchProductsCategory()
    }

    override fun onPullDownToRefresh() {
    }

    override fun onLoadMore() {
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                // Hit API to fetch products of every category
                mShopCategoriesViewModel.fetchProductsCategory()
            }
        }
    }

    override fun showProductsOfCategory(category: Filter) {
        performFragTransaction(
                CategoryProductsFragment.newInstance(category),
                CategoryProductsFragment.TAG,
                enterAnim = R.anim.slide_in_right,
                exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in,
                popExitAnim = R.anim.slide_out_right
        )
    }

    override fun onProductsClick(product: Product) {
        performFragTransaction(
                ProductDetailFragment.newInstance(product), ProductDetailFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
    }

    override fun updateFollowStatus(product: Product, category: Int) {
        // Vibrate phone
        GeneralFunctions.vibratePhone(requireContext())
        //Show Dialog in Case of Guest User else perform action.
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            showGuestModeDialog()
        } else {
            bookmarkProductStatus = product
            productCategory = category
            mShopCategoriesViewModel.addBookmark(
                    true,
                    product._id,
                    ValueMapping.onProductBookmarkAction(),
                    !product.isBookmark
            )
        }
    }

    // Show Guest Mode Dialog with help of Material Dialog by inflating custom layout.
    private fun showGuestModeDialog() {
        performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }
}