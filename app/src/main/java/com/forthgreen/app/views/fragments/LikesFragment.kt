package com.forthgreen.app.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.LikesListViewModel
import com.forthgreen.app.views.adapters.LikesAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Nandita Gandhi
 * @since 26-07-2021
 */
class LikesFragment : BaseRecyclerViewFragment(), LoadMoreListener, LikesAdapter.UserClickCallback {

    companion object {
        const val TAG = "LikesFragment"
        private const val resultSize = 50
        private const val BUNDLE_EXTRAS_POST_OR_COMMENT_REF = "BUNDLE_EXTRAS_POST_OR_COMMENT_REF"
        private const val BUNDLE_EXTRAS_TYPE = "BUNDLE_EXTRAS_TYPE"

        fun newInstance(mRef: String, isPost: Boolean = false): LikesFragment {
            return LikesFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_POST_OR_COMMENT_REF, mRef)
                    putBoolean(BUNDLE_EXTRAS_TYPE, isPost)
                }
            }
        }
    }

    // Variables
    private var page = 1

    private val mAdapter by lazy { LikesAdapter(this, this) }

    private val mLikesViewModel by lazy {
        ViewModelProvider(this).get(LikesListViewModel::class.java)
    }
    private val ref by lazy {
        requireArguments().getString(BUNDLE_EXTRAS_POST_OR_COMMENT_REF, "")
    }
    private val showPostLikes by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRAS_TYPE, false)
    }
    private val selfId: String
        get() = mLikesViewModel.getUserProfileDataFromSharedPrefs()._id

    private var userInfo = UserProfile()

    override val layoutId: Int
        get() = R.layout.fragment_likes

    override val viewModel: BaseViewModel?
        get() = mLikesViewModel

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
        // Setup toolbar title
        tvToolbarTitle.text = getString(R.string.likes_label)

        // Start shimmer
//        flShimmer.visible()
//        flShimmer.startShimmer()
//        swipeRefreshLayout.gone()

        // Fetch likes according to type
        mLikesViewModel.fetchUsers(
            isShowLoader = true, mPage = page, mResultSize = resultSize,
            mRef = ref, mLikeType = if (showPostLikes) ValueMapping.getPostLikedUsers()
            else ValueMapping.getCommentOrRepliesLikedUsers()
        )
    }

    private fun observeProperties() {
        mLikesViewModel.onSuccessfulUsersFetched().observe(viewLifecycleOwner, { usersList ->
            // Stop shimmer
//            flShimmer.stopShimmer()
//            flShimmer.gone()
//            swipeRefreshLayout.visible()

            if (usersList.data.isEmpty() && page == 1) {
                mAdapter.submitList(emptyList(), false, page, selfId)
                showNoDataText(resId = R.string.no_likes_label)
            } else {
                hideNoDataText()
                mAdapter.submitList(usersList.data, usersList.hasMore, page, selfId)
            }
        })
        mLikesViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, { statusUpdated ->
            if (statusUpdated) {
                mAdapter.updateUserInfo(userInfo)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
    }

    override fun onPullDownToRefresh() {
        // Start shimmer
//        flShimmer.visible()
//        flShimmer.startShimmer()
//        swipeRefreshLayout.gone()

        page = 1
        mLikesViewModel.fetchUsers(
            isShowLoader = false, mPage = page, mResultSize = resultSize,
            mRef = ref, mLikeType = if (showPostLikes) ValueMapping.getPostLikedUsers()
            else ValueMapping.getCommentOrRepliesLikedUsers()
        )
    }

    override fun onLoadMore() {
        page++
        mLikesViewModel.fetchUsers(isShowLoader = false, mPage = page, mResultSize = resultSize,
                mRef = ref, mLikeType = if (showPostLikes) ValueMapping.getPostLikedUsers()
        else ValueMapping.getCommentOrRepliesLikedUsers())
    }

    override fun openUserProfile(user: UserProfile) {
        performFragTransaction(OtherUserProfileFragment.newInstance(user),
                OtherUserProfileFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
    }

    override fun updateFollowStatus(user: UserProfile, follow: Boolean) {
        // Vibrate phone
        GeneralFunctions.vibratePhone(requireContext())
        // Hit the API to update follow status
        mLikesViewModel.updateUserFollowStatus(
            isShowLoading = true,
            status = follow, userRef = user._id
        )
        userInfo = user.copy(isFollowing = follow)
    }
}