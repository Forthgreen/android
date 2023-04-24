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
import com.forthgreen.app.repository.models.Restaurant
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.FollowedRestaurantsViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.FollowedRestaurantsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.fragment_followed_restaurants.*

class FollowedRestaurantsFragment : BaseLocationRecyclerViewFragment(), LoadMoreListener, FollowedRestaurantsAdapter.RestaurantClickCallback {

    companion object {
        const val TAG = "FollowedRestaurantsFragment"
        private const val resultSize = 30
    }

    //variables
    private var page = 1

    private val mFollowedRestaurantsViewModel by lazy {
        //getting viewModel
        ViewModelProvider(this).get(FollowedRestaurantsViewModel::class.java)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }
    private val coordinates
        get() = ApplicationGlobal.coordinates

    private val mAdapter by lazy { FollowedRestaurantsAdapter(this) }

    override val layoutId: Int
        get() = R.layout.fragment_followed_restaurants

    override val viewModel: BaseViewModel?
        get() = mFollowedRestaurantsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        setupViews()
        observeProperties()
    }

    private fun setupViews() {
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        if (coordinates[0] != 0.0 && coordinates[1] != 0.0) {
            if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
                //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                mFollowedRestaurantsViewModel.fetchFollowedRestaurants(isShowLoading = true,
                    mPage = page, mResultSize = resultSize, refType = ValueMapping.onRestaurantBookmarkAction(),
                    mLatitude = coordinates[0], mLongitude = coordinates[1]
                )
            }
           /* mFollowedRestaurantsViewModel.fetchFollowedRestaurants(isShowLoading = true,
                    mPage = page, mResultSize = resultSize, refType = ValueMapping.onRestaurantBookmarkAction(),
                    mLatitude = coordinates[0], mLongitude = coordinates[1]
            )*/
        } else {
            // Fetch location
            fetchCurrentLocation()
        }

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(RestaurantDetailsFragment.LOCAL_INTENT_RESTAURANT_FOLLOWED)
                    addAction(RestaurantDetailsFragment.LOCAL_INTENT_RESTAURANT_BOOKMARKED)
                    addAction(MyStuffFragment.LOCAL_INTENT_SHOW_SHIMMER)
                }
        )
    }

    private fun observeProperties() {
        mFollowedRestaurantsViewModel.onFetchedRestaurantsList().observe(viewLifecycleOwner, Observer { followedRestaurants ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()

            if (page == 1 && followedRestaurants.data.isEmpty()) {
                mAdapter.updateList(emptyList(), page, false)
                showNoDataText(R.string.no_restaurants_followed_message)
            } else {
                hideNoDataText()
                mAdapter.updateList(followedRestaurants.data, page, followedRestaurants.hasMore)
            }
        })
    }

    override fun onLocationFetchSuccess(mLatitude: Double, mLongitude: Double, mAddress: String) {
        mFollowedRestaurantsViewModel.saveUserLocation(mLatitude, mLongitude)

        // Fetch followed restaurants
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            mFollowedRestaurantsViewModel.fetchFollowedRestaurants(isShowLoading = true,
                mPage = page, mResultSize = resultSize, refType = ValueMapping.onRestaurantBookmarkAction(),
                mLatitude = mLatitude, mLongitude = mLongitude
            )
        }
       /* mFollowedRestaurantsViewModel.fetchFollowedRestaurants(isShowLoading = true,
                mPage = page, mResultSize = resultSize, refType = ValueMapping.onRestaurantBookmarkAction(),
                mLatitude = mLatitude, mLongitude = mLongitude
        )*/
    }

    override fun onPullDownToRefresh() {
        page = 1
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            mFollowedRestaurantsViewModel.fetchFollowedRestaurants(
                isShowLoading = false, mPage = page, mResultSize = resultSize,
                refType = ValueMapping.onRestaurantBookmarkAction(),
                mLatitude = coordinates[0], mLongitude = coordinates[1]
            )
        }
       /* mFollowedRestaurantsViewModel.fetchFollowedRestaurants(
                isShowLoading = false, mPage = page, mResultSize = resultSize,
                refType = ValueMapping.onRestaurantBookmarkAction(),
                mLatitude = coordinates[0], mLongitude = coordinates[1]
        )*/
    }

    override fun onLoadMore() {
        page++
        if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
            //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            mFollowedRestaurantsViewModel.fetchFollowedRestaurants(
                isShowLoading = false, mPage = page, mResultSize = resultSize,
                refType = ValueMapping.onRestaurantBookmarkAction(),
                mLatitude = coordinates[0], mLongitude = coordinates[1]
            )
        }
       /* mFollowedRestaurantsViewModel.fetchFollowedRestaurants(
                isShowLoading = false, mPage = page, mResultSize = resultSize,
                refType = ValueMapping.onRestaurantBookmarkAction(),
                mLatitude = coordinates[0], mLongitude = coordinates[1]
        )*/
    }

    override fun openDetails(restaurant: Restaurant) {
        performFragTransaction(
                RestaurantDetailsFragment.newInstance(restaurant), RestaurantDetailsFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
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

        if (coordinates[0] != 0.0 && coordinates[1] != 0.0) {
            if (ApplicationGlobal.isLoggedIn != ValueMapping.getUserAccessGuest()) {
                //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                mFollowedRestaurantsViewModel.fetchFollowedRestaurants(
                    isShowLoading = false, mPage = page, mResultSize = resultSize,
                    refType = ValueMapping.onRestaurantBookmarkAction(),
                    mLatitude = coordinates[0], mLongitude = coordinates[1]
                )
            }
           /* mFollowedRestaurantsViewModel.fetchFollowedRestaurants(isShowLoading = true,
                    mPage = page, mResultSize = resultSize, refType = ValueMapping.onRestaurantBookmarkAction(),
                    mLatitude = coordinates[0], mLongitude = coordinates[1]
            )*/
        } else {
            // Fetch location
            fetchCurrentLocation()
        }
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}