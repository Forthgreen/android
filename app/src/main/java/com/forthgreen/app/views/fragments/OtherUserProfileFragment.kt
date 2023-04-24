package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ProfileDetailsViewModel
import com.forthgreen.app.views.adapters.PostsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.bottom_sheet_menu_options.tvCancel
import kotlinx.android.synthetic.main.dialog_block_user.*
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.dialog_review_menu.tvBlockUser
import kotlinx.android.synthetic.main.dialog_review_menu.tvReportAbuse
import kotlinx.android.synthetic.main.fragment_other_user_profile.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Nandita Gandhi
 * @since 15-04-2021
 */
class OtherUserProfileFragment : BaseRecyclerViewFragment(), LoadMoreListener, PostsAdapter.PostsClickCallback {

    companion object {
        const val TAG = "OtherUserProfileFragment"
        private const val BUNDLE_EXTRAS_USER_INFO = "BUNDLE_EXTRAS_USER_INFO"
        private const val resultSize = 50
        const val LOCAL_INTENT_USER_POST_ACTION_PERFORMED = "LOCAL_INTENT_USER_POST_ACTION_PERFORMED"
        const val INTENT_EXTRAS_FOLLOW_STATUS = "INTENT_EXTRAS_FOLLOW_STATUS"

        fun newInstance(user: UserProfile): OtherUserProfileFragment {
            return OtherUserProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRAS_USER_INFO, user)
                }
            }
        }
    }

    private var userPostList: List<HomeFeed> = emptyList()

    // Variables
    private var page = 1

    private val mUserDetails by lazy {
        requireArguments().getParcelable<UserProfile>(BUNDLE_EXTRAS_USER_INFO)!!
    }
    private val mProfileDetailsViewModel by lazy {
        ViewModelProvider(this).get(ProfileDetailsViewModel::class.java)
    }

    private val mAdapter by lazy { PostsAdapter(this, this) }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }
    private var post = HomeFeed()

    private val selfId: String
        get() = mProfileDetailsViewModel.getUserProfileDataFromSharedPrefs()._id

    private var userData = UserProfile()

    override val layoutId: Int
        get() = R.layout.fragment_other_user_profile

    override val viewModel: BaseViewModel?
        get() = mProfileDetailsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    var userProfile= UserProfile()

    private lateinit var noDataTextView: TextView

    private var blockedUserTextView: String = "Block user"
    private var follow: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noDataTextView = view.findViewById<TextView>(R.id.tvNoData)
    }

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Setup toolbar
        tvToolbarTitle.text = mUserDetails.firstName
        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_horizontal_menu)
        }

        // Show menu icon according to user
        if (mUserDetails.dummyUser || selfId == mUserDetails._id) {
            ivToolbarActionEnd.gone()
        } else {
            ivToolbarActionEnd.visible()
        }

        // Push Down anim
        PushDownAnim.setPushDownAnimTo(ivToolbarActionEnd)

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        // Update the views via adapter
        mAdapter.submitDetails(emptyList(), mUserDetails, false, page, selfId)

        // Fetch User details
        mProfileDetailsViewModel.fetchUserDetails(false, mUserDetails._id, page, resultSize)

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, IntentFilter().apply {
            addAction(CommentsListFragment.LOCAL_INTENT_COMMENTS_ACTION)
            addAction(RepliesFragment.LOCAL_INTENT_REPLY_COMMENTS_ACTION)
        })
    }

    private fun setupListeners() {
        ivToolbarActionEnd.setOnClickListener {
            // Open Custom Dialog
            MaterialDialog(requireContext()).show {
                customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                cancelOnTouchOutside(false)
                cancelable(false)
                cornerRadius(res = R.dimen.dialog_corner_radius)
                tvBlockUser.text = blockedUserTextView
                if (blockedUserTextView != "") {
                    tvBlockUser.visible()
                }
                tvBlockUser.setOnClickListener {
                    this.dismiss()

                    if (blockedUserTextView != "Unblock user") {
                        MaterialDialog(requireContext()).show {
                            customView(R.layout.dialog_block_user, dialogWrapContent = true, noVerticalPadding = true)
                            cancelOnTouchOutside(false)
                            cancelable(false)
                            cornerRadius(res = R.dimen.dialog_corner_radius)
                            tvCancel.setOnClickListener {
                                this.dismiss()
                            }
                            tvBlock.setOnClickListener {
                                this.dismiss()
                                mProfileDetailsViewModel.updateUserBlockStatus(
                                    isShowLoading = true,
                                    status = follow,
                                    userRef = mUserDetails._id
                                )
                            }
                        }
                    } else {
                        mProfileDetailsViewModel.updateUserBlockStatus(
                            isShowLoading = true,
                            status = follow,
                            userRef = mUserDetails._id
                        )
                    }
                }
                tvReportAbuse.apply {
                    text = getString(R.string.report_user)
                  //  setTextColor(ContextCompat.getColor(requireContext(), R.color.colorReportAbuse))
                }
                tvReportAbuse.setOnClickListener {
                    this.dismiss()
                    performFragTransaction(ReportProfileFragment.newInstance(mUserDetails._id), ReportProfileFragment.TAG)
                }
                tvCancel.setOnClickListener {
                    this.dismiss()
                }
            }
        }
    }

    private fun observeProperties() {
        mProfileDetailsViewModel.onFetchedProfileDetails().observe(viewLifecycleOwner, { fetchedDetails ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()
            tvToolbarTitle.text = fetchedDetails.data.firstName
            userProfile = fetchedDetails.data
            userPostList = fetchedDetails.data.posts

            if (userProfile.isBlock && userProfile.isSenderBlock.isEmpty()) {
                follow = false
                blockedUserTextView = requireContext().getString(R.string.unblocked_user)
                noDataTextView.text = requireContext().getString(R.string.this_user_has_blocked_you)
                noDataTextView.visible()
                val userProfileBlocked = userProfile.copy(isBlock = true)
                mAdapter.submitDetails(emptyList(), userProfileBlocked, false, page, selfId)
            } else if (userProfile.isSenderBlock.isNotEmpty()){
                follow = false
                blockedUserTextView = ""
                val userProfileBlocked = userProfile.copy(isBlock = true)
                noDataTextView.text = requireContext().getString(R.string.this_user_has_blocked_you)
                noDataTextView.visible()
                mAdapter.submitDetails(emptyList(), userProfileBlocked, false, page, selfId)
            } else {
                mAdapter.submitDetails(fetchedDetails.data.posts, fetchedDetails.data, fetchedDetails.hasMore, page, selfId)
            }
          //  mAdapter.submitDetails(fetchedDetails.data.posts, fetchedDetails.data, fetchedDetails.hasMore, page, selfId)
        })
        mProfileDetailsViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, { statusUpdated ->
            if (statusUpdated) {
                mAdapter.updateUser(userData)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                    Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED).apply {
                        putExtra(INTENT_EXTRAS_FOLLOW_STATUS, userData.isFollow)
                    }
                )
            }
        })
        mProfileDetailsViewModel.onBlockStatusUpdated().observe(viewLifecycleOwner, { statusUserBlockedUpdated ->
            if (statusUserBlockedUpdated) {
                follow = !follow
                if (follow) {
                    blockedUserTextView = requireContext().getString(R.string.block_user)
                    val userProfileBlocked = userProfile.copy(isBlock = false)
                    noDataTextView.gone()
                    mAdapter.submitDetails(userPostList, userProfileBlocked, false, page, selfId)
                } else {
                    blockedUserTextView = requireContext().getString(R.string.unblocked_user)
                    val userProfileBlocked = userProfile.copy(isBlock = true)
                    noDataTextView.text = requireContext().getString(R.string.you_have_blocked_this_user)
                    noDataTextView.visible()
                    mAdapter.submitDetails(emptyList(), userProfileBlocked, false, page, selfId)
                }
            }
        })
        mProfileDetailsViewModel.onPostLikeUpdated().observe(viewLifecycleOwner, { postLikeUpdated ->
            if (postLikeUpdated) {
                mAdapter.updatePost(post)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_USER_POST_ACTION_PERFORMED))
            }
        })
        mProfileDetailsViewModel.onPostReported().observe(viewLifecycleOwner, { reported ->
            if (reported) {
                performFragTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })
        mProfileDetailsViewModel.onPostDeleted().observe(viewLifecycleOwner, { deleted ->
            if (deleted) {
                mAdapter.removePost(post)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_USER_POST_ACTION_PERFORMED))
            }
        })
    }

    override fun onPullDownToRefresh() {
        page = 1
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()
        mProfileDetailsViewModel.fetchUserDetails(false, mUserDetails._id, page, resultSize)

    }

    override fun onLoadMore() {
        page++
        mProfileDetailsViewModel.fetchUserDetails(false, mUserDetails._id, page, resultSize)
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                onPullDownToRefresh()
            }
        }
    }

    override fun performPostClickAction(postInfo: HomeFeed, clickType: Int, user: UserProfile, postLiked: Boolean) {
        when (clickType) {
            ValueMapping.onMenuClick() -> {
                // Open Custom Dialog
                MaterialDialog(requireContext()).show {
                    customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                    cancelOnTouchOutside(false)
                    cancelable(false)
                    cornerRadius(res = R.dimen.dialog_corner_radius)
                    if (selfId == user._id) {
                        tvReportAbuse.apply {
                            text = getString(R.string.remove_post_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                mProfileDetailsViewModel.deletePost(mShowLoader = true, postRef = postInfo._id)
                                post = postInfo
                            }
                        }
                    } else {
                        tvReportAbuse.apply {
                            text = getString(R.string.report_post_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                mProfileDetailsViewModel.reportPost(mShowLoader = true, postInfo._id)
                            }
                        }
                    }
                    tvCancel.setOnClickListener {
                        this.dismiss()
                    }
                }
            }
            ValueMapping.onLikeOrDislike() -> {
                // Vibrate phone
                GeneralFunctions.vibratePhone(requireContext())
                mProfileDetailsViewModel.updatePostLike(
                    mShowLoader = true,
                    postRef = postInfo._id,
                    liked = postLiked
                )
                post = if (postLiked) {
                    postInfo.copy(isLike = postLiked, likes = postInfo.likes + 1)
                } else {
                    postInfo.copy(isLike = postLiked, likes = postInfo.likes - 1)
                }
            }
            ValueMapping.onCommentsOrRepliesClick() -> {
                ApplicationGlobal.muteVideo = true
                mAdapter.pauseVideo()
                performFragTransaction(
                    CommentsListFragment.newInstance(postInfo._id),
                    CommentsListFragment.TAG,
                    enterAnim = R.anim.slide_in_right,
                    exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in,
                    popExitAnim = R.anim.slide_out_right)
            }
            ValueMapping.onLikesClick() -> {
                performFragTransaction(LikesFragment.newInstance(postInfo._id, true), LikesFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
            }
        }
    }

    override fun performDetailsClickAction(user: UserProfile, follow: Boolean, clickType: Int) {
        when (clickType) {
            ValueMapping.onFollowersClick() -> {
                performFragTransaction(FollowersFollowingTabFragment.newInstance(user, true), FollowersFollowingTabFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
            ValueMapping.onFollowingClick() -> {
                performFragTransaction(FollowersFollowingTabFragment.newInstance(user, false), FollowersFollowingTabFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
            else -> {
                // Update the follow status via API hit
                mProfileDetailsViewModel.updateUserFollowStatus(
                    isShowLoading = true,
                    status = follow,
                    userRef = user._id
                )
                userData = if (follow) {
                    user.copy(isFollow = follow, followers = user.followers + 1)
                } else {
                    user.copy(isFollow = follow, followers = user.followers - 1)
                }
            }
        }
    }

    override fun onTaggedUserClick(taggedUser: Users) {
        when (taggedUser.type) {
            ValueMapping.onUserTaggedAction() -> performFragTransaction(
                OtherUserProfileFragment.newInstance(
                    UserProfile(
                        _id = taggedUser._id,
                        image = taggedUser.image,
                        username = taggedUser.name
                    )
                ), OtherUserProfileFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )

            ValueMapping.onBrandTaggedAction() -> performFragTransaction(
                BrandDetailFragment.newInstance(
                    Brand(
                        _id = taggedUser._id,
                        coverImage = taggedUser.image,
                        brandName = taggedUser.name
                    )
                ), BrandDetailFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )

            ValueMapping.onRestaurantTaggedAction() -> performFragTransaction(
                RestaurantDetailsFragment.newInstance(
                    Restaurant(
                        _id = taggedUser._id,
                        thumbnail = taggedUser.image,
                        name = taggedUser.name
                    )
                ), RestaurantDetailsFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )
        }
    }

    override fun onMuteUnmuteClicked(post: HomeFeed) {
        mAdapter.muteStatusChanged(post)
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}