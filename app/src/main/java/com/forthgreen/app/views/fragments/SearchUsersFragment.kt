package com.forthgreen.app.views.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SearchUsersViewModel
import com.forthgreen.app.views.adapters.SearchUsersAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_search_users.*

/**
 * @author Nandita Gandhi
 * @since 12-04-2021
 */
class SearchUsersFragment : BaseRecyclerViewFragment(), SearchUsersAdapter.UserClickCallback,
        LoadMoreListener {

    companion object {
        const val TAG = "SearchUsersFragment"
        private const val mResultSize = 75
    }

    // Variables
    private var page = 1
    private val mAdapter by lazy { SearchUsersAdapter(this, this) }
    private val mSearchUsersViewModel by lazy {
        // Get View Model
        ViewModelProvider(this).get(SearchUsersViewModel::class.java)
    }
    private val searchText: String
        get() = etSearchUsers.trimmedText

    private var mUser = UserProfile()

    private val selfId: String
        get() = mSearchUsersViewModel.getUserProfileDataFromSharedPrefs()._id

    override val layoutId: Int
        get() = R.layout.fragment_search_users

    override val viewModel: BaseViewModel?
        get() = mSearchUsersViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Push down anim
        PushDownAnim.setPushDownAnimTo(tvCancelSearch)

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        // Search all users firstly when searched text is empty.
        mSearchUsersViewModel.searchUsers(true, page, mResultSize, searchText)
    }

    private fun setupListeners() {
        etSearchUsers.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                // Start shimmer
                flShimmer.startShimmer()
                flShimmer.visible()
                swipeRefreshLayout.gone()
                mSearchUsersViewModel.searchUsers(true, page, mResultSize, text)
            } else {
                mAdapter.submitList(emptyList(), selfId, page, false)
            }
        }
        tvCancelSearch.setOnClickListener {
            supportFragmentManager().popBackStack()
        }
    }

    private fun observeProperties() {
        mSearchUsersViewModel.onSuccessfulUsersFetched().observe(viewLifecycleOwner, { fetchedUsers ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()

            if (fetchedUsers.data.isEmpty() && page == 1) {
                mAdapter.submitList(emptyList(), selfId, page, hasMore = false)
                showNoDataText(resId = R.string.no_data_to_show)
            } else {
                hideNoDataText()
                mAdapter.submitList(fetchedUsers.data, selfId, page, hasMore = fetchedUsers.hasMore)
            }
        })
        mSearchUsersViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, { statusUpdated ->
            if (statusUpdated) {
                // Update follow status via adapter
                mAdapter.updateUser(mUser)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
    }

    override fun onPullDownToRefresh() {
        page = 1
        if (searchText.isNotEmpty()) {
            // Start shimmer
            flShimmer.startShimmer()
            flShimmer.visible()
            swipeRefreshLayout.gone()
            mSearchUsersViewModel.searchUsers(false, page, mResultSize, searchText)
        } else {
            hideSwipeRefreshLoader()
        }
    }

    override fun onLoadMore() {
        page++
        mSearchUsersViewModel.searchUsers(false, page, mResultSize, searchText)
    }

    override fun performUserClickAction(userData: UserProfile, openProfile: Boolean, follow: Boolean) {
        if (openProfile) {
            // Open profile of user
            performFragTransaction(OtherUserProfileFragment.newInstance(userData), OtherUserProfileFragment.TAG)
        } else {
            // Hit the follow API
            mSearchUsersViewModel.updateUserFollowStatus(isShowLoading = true, status = follow, userRef = userData._id)
            mUser = userData.copy(isFollow = follow)
        }
    }

    override fun onStop() {
        hideKeyboard()
        super.onStop()
    }
}