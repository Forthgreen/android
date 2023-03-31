package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.FollowListViewModel
import com.forthgreen.app.views.adapters.FollowersFollowingListingAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener

/**
 * @author Nandita Gandhi
 * @since 15-04-2021
 */
class FollowersFollowingListingFragment : BaseRecyclerViewFragment(), LoadMoreListener, FollowersFollowingListingAdapter.UserClickCallback {

    companion object {
        const val TAG = "FollowersFollowingListingFragment"
        private const val LISTING_RESULT_SIZE = 50
        private const val BUNDLE_EXTRAS_LIST_TYPE = "BUNDLE_EXTRAS_LIST_TYPE"
        private const val BUNDLE_EXTRAS_USER_ID = "BUNDLE_EXTRAS_USER_ID"

        fun newInstance(isFollowingList: Boolean, userID: String): FollowersFollowingListingFragment {
            return FollowersFollowingListingFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(BUNDLE_EXTRAS_LIST_TYPE, isFollowingList)
                    putString(BUNDLE_EXTRAS_USER_ID, userID)
                }
            }
        }
    }

    // Variables
    private var page = 1

    private val isFollowingListing by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRAS_LIST_TYPE, true)
    }

    private val userId by lazy {
        requireArguments().getString(BUNDLE_EXTRAS_USER_ID, "")
    }

    private val mAdapter by lazy {
        FollowersFollowingListingAdapter(this, this)
    }

    private val mFollowListViewModel by lazy {
        // Get ViewModel
        ViewModelProvider(this).get(FollowListViewModel::class.java)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private var userData = UserProfile()        // Top level user object.

    private val selfId: String
        get() = mFollowListViewModel.getUserProfileDataFromSharedPrefs()._id


    override val layoutId: Int
        get() = R.layout.fragment_followers_following_listing

    override val viewModel: BaseViewModel?
        get() = mFollowListViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setupViews()
        observeProperties()
    }

    private fun setupViews() {
        // Start shimmer
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()

        // Fetch followers and following listing
        mFollowListViewModel.getFollowList(
            isShowLoading = false, mPage = page,
            mResultSize = LISTING_RESULT_SIZE, mFollowingList = isFollowingListing, mUserId = userId
        )

        // Register Broadcast Rec
        mLocalBroadcastManager.registerReceiver(
            mLocalBroadcastReceiver,
            IntentFilter().apply {
                addAction(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED)
            }
        )
    }

    private fun observeProperties() {
        mFollowListViewModel.onFollowListFetched().observe(viewLifecycleOwner, { followList ->
            // Stop shimmer
//            flShimmer.stopShimmer()
//            flShimmer.gone()
//            swipeRefreshLayout.visible()
            if (followList.data.isEmpty() && page == 1) {
                mAdapter.submitList(
                    emptyList(), selfID = selfId,
                    false, page
                )
            } else {
                hideNoDataText()
                mAdapter.submitList(
                    followList.data, selfID = selfId,
                    followList.hasMore, page
                )
            }
        })
        mFollowListViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, { statusUpdated ->
            if (statusUpdated) {
                mAdapter.updateUser(userData)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                // If we receive a local broadcast that a user was followed/ unfollowed then update
                // its corresponding status
                if (intent.action == PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED &&
                        intent.hasExtra(OtherUserProfileFragment.INTENT_EXTRAS_FOLLOW_STATUS)) {
                    val status = intent.getBooleanExtra(
                            OtherUserProfileFragment.INTENT_EXTRAS_FOLLOW_STATUS, false)
                    // Update User Profile for that specific user.
                    mAdapter.updateUser(userData.copy(isFollow = status))
                }
            }
        }
    }

    override fun onPullDownToRefresh() {
        page = 1
        // Start shimmer
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()
        mFollowListViewModel.getFollowList(
            false,
            page,
            LISTING_RESULT_SIZE,
            isFollowingListing,
            userId
        )
    }

    override fun onLoadMore() {
        page++
        mFollowListViewModel.getFollowList(false, page, LISTING_RESULT_SIZE, isFollowingListing, userId)
    }

    override fun performClickAction(user: UserProfile, follow: Boolean) {
        // Hit the API to update the follow status
        mFollowListViewModel.updateUserFollowStatus(isShowLoading = true, status = follow, userRef = user._id)
        userData = user.copy(isFollow = follow)
    }

    override fun openUserProfile(user: UserProfile) {
        // Assign to top level user model.
        userData = user
        performFragTransaction(OtherUserProfileFragment.newInstance(user),
                OtherUserProfileFragment.TAG)
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}