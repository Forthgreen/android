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
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.MyProductsViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.MyProductListAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.fragment_my_products.*

class MyProductsFragment : BaseRecyclerViewFragment(), LoadMoreListener, MyProductListAdapter.ProductDetailCallback {

    companion object {
        const val TAG = "MyBrandsFragment"
    }

    //variables
    private var mPage = 1

    private val resultSize = 30

    private val mMyProductsViewModel: MyProductsViewModel by lazy {
        ViewModelProvider(this).get(MyProductsViewModel::class.java)
    }

    private val mProductListAdapter by lazy { MyProductListAdapter(this) }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_my_products

    override val viewModel: BaseViewModel?
        get() = mMyProductsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mProductListAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true


    override fun setData() {
        //Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(BrandDetailFragment.LOCAL_INTENT_BRAND_FOLLOWED)
                    addAction(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED)
                    addAction(ShopCategoriesFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED)
                    addAction(MyStuffFragment.LOCAL_INTENT_SHOW_SHIMMER)
                }
        )

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

       /* // Get Products
        mMyProductsViewModel.getBookmarkedProducts(
                true,
                mPage,
                resultSize,
                ValueMapping.onProductBookmarkAction()
        )*/
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mMyProductsViewModel.getBookmarkedProducts(
                true,
                mPage,
                resultSize,
                ValueMapping.onProductBookmarkAction()
            )
        }
        observeProperties()
    }

    private fun observeProperties() {
        mMyProductsViewModel.onReceivingMyStuff().observe(viewLifecycleOwner, Observer { products ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()

            if (mPage == 1 && products.data.isEmpty()) {
                mProductListAdapter.updateList(emptyList(), mPage, false)
                showNoDataText(R.string.no_products_followed_message)
            } else {
                hideNoDataText()
                mProductListAdapter.updateList(products.data, mPage, false)
            }
        })
    }

    override fun onPullDownToRefresh() {
        mPage = 1
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mMyProductsViewModel.getBookmarkedProducts(
                true,
                mPage,
                resultSize,
                ValueMapping.onProductBookmarkAction()
            )
        }
    }

    override fun onLoadMore() {
        mPage++
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mMyProductsViewModel.getBookmarkedProducts(
                true,
                mPage,
                resultSize,
                ValueMapping.onProductBookmarkAction()
            )
        }

    }

    private fun showShimmer() {
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        // Get Products
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //get brands
            mMyProductsViewModel.getBookmarkedProducts(
                true,
                mPage,
                resultSize,
                ValueMapping.onProductBookmarkAction()
            )
        }
    }

    override fun showProductDetail(product: Product) {
        performFragTransaction(
                ProductDetailFragment.newInstance(product), ProductDetailFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(
                supportFragmentManager(),
                R.id.flFragContainer,
                frag,
                fragmentTag,
                isAddFragment = false
        )
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

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}