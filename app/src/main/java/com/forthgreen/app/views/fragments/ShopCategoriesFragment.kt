package com.forthgreen.app.views.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ShopCategoriesViewModel
import com.forthgreen.app.views.adapters.CategoryListAdapter
import com.forthgreen.app.views.adapters.ShopCategoriesAdapter
import com.forthgreen.app.views.adapters.ShopFeedAdapter
import com.forthgreen.app.views.adapters.ShopProductsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_shop_categories.flShimmer
import kotlinx.android.synthetic.main.fragment_shop_categories.llLoadMore
import kotlinx.android.synthetic.main.fragment_shop_categories.recyclerView
import kotlinx.android.synthetic.main.fragment_shop_categories.recyclerViewCategoryList
import kotlinx.android.synthetic.main.fragment_shop_categories.toolbar
import kotlinx.android.synthetic.main.fragment_shop_categories_new.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.toolbar


/**
 * @author Nandita Gandhi
 * @since 05-Aug-2021
 */
class ShopCategoriesFragment : BaseRecyclerViewFragment(), LoadMoreListener,
    ShopCategoriesAdapter.CategoryClickCallback,
    ShopProductsAdapter.ProductsCallback, ShopFeedAdapter.ProductsCallback,
    CategoryListAdapter.CategoryClickCallback {

    companion object {
        const val TAG = "ShopCategoriesFragment"
        const val LOCAL_INTENT_PRODUCT_BOOKMARKED = "LOCAL_INTENT_PRODUCT_BOOKMARKED"
    }

    private var list: ArrayList<Product>? = arrayListOf()
    private var mPaginationEnabled = true
    private var loading = false
    private var page: Int = 0
    private var isCategoryRecylerViewShowing: Boolean = false

    // Variables
    // private val mAdapter by lazy { ShopCategoriesAdapter(this, this) }

    private val mAdapter by lazy { ShopFeedAdapter(this, this) }
    private val mCategoryListAdapter by lazy { CategoryListAdapter(this) }

    private var bookmarkProductStatus = Product()
    private var productCategory: Int = 0
    //  private val listOfProducts = mutableListOf<ProductCategory>()

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    private val mShopCategoriesViewModel by lazy {
        ViewModelProvider(this).get(ShopCategoriesViewModel::class.java)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_shop_categories_new

    private lateinit var mGridlayoutManager: GridLayoutManager

    override val viewModel: BaseViewModel?
        get() = mShopCategoriesViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = mGridlayoutManager

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

        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_category_search_list)
            visible()
        }

        // Set up Linear Layout Manager
        mLinearLayoutManager = LinearLayoutManager(requireContext())

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        recyclerView.gone()

        // Initialise layout manager
        mGridlayoutManager = GridLayoutManager(activity, 2)

        mGridlayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mAdapter.getItemViewType(position)) {
                    ShopFeedAdapter.VIEW_TYPE_HEADER -> 2
                    else -> 1
                }
            }
        }

        recyclerView.layoutManager = mGridlayoutManager
        recyclerView.adapter = mAdapter
        recyclerView.setHasFixedSize(true)

        /* recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
             override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                 var pastVisiblesItems = 0
                 var visibleItemCount: Int = 0
                 var totalItemCount: Int = 0
                 if (dy > 0) //check for scroll down
                 {
                     visibleItemCount = mGridlayoutManager.getChildCount()
                     totalItemCount = mGridlayoutManager.getItemCount()
                     pastVisiblesItems = mGridlayoutManager.findFirstVisibleItemPosition()
                     if (mPaginationEnabled && !loading) {
                         if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                             loading = true
                             page++

                             Handler().postDelayed({
                                 loadNextItems()
                             },2000)

                         }
                     }
                 }
             }
         })*/


        nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
            val diff: Int = view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)
            if (diff == 0) {
                val activity: Activity? = activity
                if (isAdded && activity != null) {
                    if (mPaginationEnabled && !loading) {
                        llLoadMore.visible()
                        loading = true
                        page++
                        loadNextItems()
                    }
                }
            }
        }

        // Hit API to fetch products of every category
        // mShopCategoriesViewModel.fetchProductsCategory(false, 1, 23)

        // Receive broadcast
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
            IntentFilter().apply {
                addAction(ProductDetailFragment.BOOKMARK_INTENT_STATUS_UPDATED)
                addAction(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED)
            })
    }

    private fun loadNextItems() {
        // mAdapter.removeLoadingFooter()
        mShopCategoriesViewModel.fetchProductsCategory(false, page, 23)
    }

    private fun setupListeners() {
        /* viewSearch.setOnClickListener {
             performFragTransaction(HomeSearchFragment(), HomeSearchFragment.TAG)
         }*/

        ivToolbarActionEnd.setOnClickListener {
            llLoadMore.gone()
            nestedScrollView.gone()

            if (!isCategoryRecylerViewShowing) {
                ivToolbarActionEnd.apply {
                    setImageResource(R.drawable.ic_category_list)
                }
                recyclerView.gone()
                recyclerViewCategoryList.visible()
                recyclerViewCategoryList.layoutManager = mLinearLayoutManager
                recyclerViewCategoryList.adapter = mCategoryListAdapter
            } else {
                nestedScrollView.visible()
                ivToolbarActionEnd.apply {
                    setImageResource(R.drawable.ic_category_search_list)
                }
                recyclerView.visible()
                recyclerViewCategoryList.gone()
            }
            isCategoryRecylerViewShowing = !isCategoryRecylerViewShowing
        }
    }

    private fun observeProperties() {
        val categoriesList = arrayListOf<Filter>()
        mShopCategoriesViewModel.onCategoryFetched()
            .observe(viewLifecycleOwner, { productCategory ->

                flShimmer.stopShimmer()
                llLoadMore.visible()
                flShimmer.gone()
                nestedScrollView.visible()
                recyclerView.visible()
                recyclerViewCategoryList.gone()
                mPaginationEnabled = productCategory.hasMore == true
                list!!.clear()

                productCategory.data?.let { list!!.addAll(it) }
                //  val list = getData(productCategory.data)
                mAdapter.submitList(list!!, productCategory.hasMore!!, page)
                llLoadMore.gone()
                loading = false;

                /* val clothingProds = productCategory.data.filter { category -> category._id == ValueMapping.onClothingCategorySelected() }
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
     mShopCategoriesViewModel  */
            })



        mShopCategoriesViewModel.onBookmarkAddSuccess().observe(viewLifecycleOwner, { success ->
            if (success) {
/*                val productsList = mutableListOf<Product>()
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
                    .sendBroadcast(Intent(LOCAL_INTENT_PRODUCT_BOOKMARKED))*/

                mAdapter.updateProduct(bookmarkProductStatus.copy(isBookmark = !bookmarkProductStatus.isBookmark))
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED))
            }
        })
    }

    fun scrollToTop() {

        nestedScrollView.fling(0);
        nestedScrollView.smoothScrollTo(0, 0);

        recyclerView.getAdapter()?.let {
            recyclerView.getLayoutManager()?.smoothScrollToPosition(
                recyclerView,
                RecyclerView.State(),
                0
            )
        }
        recyclerViewCategoryList.getAdapter()?.let {
            recyclerViewCategoryList.getLayoutManager()?.smoothScrollToPosition(
                recyclerViewCategoryList,
                RecyclerView.State(),
                0
            )
        }
    }

    fun showShimmer() {
        isCategoryRecylerViewShowing = false
        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_category_search_list)
        }
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        recyclerView.gone()
        // Hit API to fetch products of every category
        //   mShopCategoriesViewModel.fetchProductsCategory(false, 1, 23)
        list?.clear()
        mAdapter.clearList()
        page = 1
        mPaginationEnabled = false
        loading = true

        mShopCategoriesViewModel.fetchProductsCategory(false, page, 23)
    }

    override fun onPullDownToRefresh() {
    }

    override fun onLoadMore() {
        Log.d("called", "called")
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null && intent.extras != null) {
                val productString = intent.extras!!.getString("product")
                // Hit API to fetch products of every category
                val product = Gson().fromJson(productString, Product::class.java)
                bookmarkProductStatus = product

                mAdapter.updateProduct(bookmarkProductStatus)
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED))

                //We commented
                //mShopCategoriesViewModel.fetchProductsCategory(false, 1, 23)
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

    override fun updateFollowStatus(product: Product) {
        // Vibrate phone
        GeneralFunctions.vibratePhone(requireContext())
        //Show Dialog in Case of Guest User else perform action.
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            showGuestModeDialog()
        } else {
            bookmarkProductStatus = product
            // productCategory = category
            mShopCategoriesViewModel.addBookmark(
                false,
                product._id,
                ValueMapping.onProductBookmarkAction(),
                !product.isBookmark
            )
        }
    }

    override fun onBrandProductImageClick(product: Product) {
        if (product.brandRef.isNotEmpty()) {
            val brand = Brand(
                _id = product.brandRef,
                brandName = product.brandName,
                logo = product.logo,
                coverImage = product.coverImage
            )
            performFragTransaction(
                BrandDetailFragment.newInstance(brand),
                BrandDetailFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )
        }
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