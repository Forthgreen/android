package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.forthgreen.app.workers.CreatePostWorker
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.bottom_sheet_menu_options.tvCancel
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_self_profile.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Nandita Gandhi
 * @since 17-04-2021
 */
class SelfProfileFragment : BaseRecyclerViewFragment(), LoadMoreListener, PostsAdapter.PostsClickCallback {

    companion object {
        const val TAG = "ProfileFragment"
        private const val resultSize = 50
        const val LOCAL_INTENT_SELF_POST_ACTION_PERFORMED = "LOCAL_INTENT_SELF_POST_ACTION_PERFORMED"
    }

    // Variables
    private var page = 1

    private val mAdapter by lazy { PostsAdapter(this, this) }

    private val mProfileDetailsViewModel by lazy {
        ViewModelProvider(this).get(ProfileDetailsViewModel::class.java)
    }
    private val selfDetails
        get() = mProfileDetailsViewModel.getUserProfileDataFromSharedPrefs()

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }
    private var post = HomeFeed()

    override val layoutId: Int
        get() = R.layout.fragment_self_profile

    override val viewModel: BaseViewModel?
        get() = mProfileDetailsViewModel

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
        // Set up toolbar
        tvToolbarTitle.text = selfDetails.firstName
        toolbar.setNavigationIcon(R.drawable.ic_profile_details)
        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_share)
            visible()
        }

        // Push Down anim
        PushDownAnim.setPushDownAnimTo(ivToolbarActionEnd)

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        mAdapter.submitDetails(selfDetails.posts, selfDetails, false, page, selfDetails._id)

        // Fetch details
        mProfileDetailsViewModel.fetchUserDetails(false, mPage = page, mResultSize = resultSize)

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, IntentFilter().apply {
            addAction(EditProfileFragment.LOCAL_INTENT_PROFILE_UPDATED)
            addAction(CommentsListFragment.LOCAL_INTENT_COMMENTS_ACTION)
            addAction(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED)
            addAction(OtherUserProfileFragment.LOCAL_INTENT_USER_POST_ACTION_PERFORMED)
            addAction(NotificationDetailsFragment.LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
            addAction(CreatePostWorker.LOCAL_INTENT_POST_CREATED)
            addAction(RepliesFragment.LOCAL_INTENT_REPLY_COMMENTS_ACTION)
        })
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            drawerCallbacks.openNavDrawer()
        }
        ivToolbarActionEnd.setOnClickListener {
            GeneralFunctions.shareGenericDeepLink(requireContext(), selfDetails.firstName, selfDetails.bio, GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), selfDetails.image), Gson().toJson(selfDetails), ValueMapping.deepLinkingTypeProfile())
        }
    }

    private fun observeProperties() {
        mProfileDetailsViewModel.onFetchedProfileDetails().observe(viewLifecycleOwner, { fetchedDetails ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()
            mAdapter.submitDetails(
                    fetchedDetails.data.posts,
                    fetchedDetails.data,
                    fetchedDetails.hasMore,
                    page,
                    selfDetails._id
            )
        })
        mProfileDetailsViewModel.onPostDeleted().observe(viewLifecycleOwner, { deleted ->
            if (deleted) {
                mAdapter.removePost(post)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_SELF_POST_ACTION_PERFORMED))
            }
        })
        mProfileDetailsViewModel.onPostLikeUpdated().observe(viewLifecycleOwner, { postLikeUpdated ->
            if (postLikeUpdated) {
                mAdapter.updatePost(post)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_SELF_POST_ACTION_PERFORMED))
            }
        })
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                onPullDownToRefresh()
            }
        }
    }

    override fun onPullDownToRefresh() {
        page = 1
        // Start shimmer
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()
        mProfileDetailsViewModel.fetchUserDetails(false, mPage = page, mResultSize = resultSize)
    }

    override fun onLoadMore() {
        page++
        mProfileDetailsViewModel.fetchUserDetails(false, mPage = page, mResultSize = resultSize)
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
                    tvReportAbuse.apply {
                        text = getString(R.string.remove_post_label)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                        setOnClickListener {
                            this@show.dismiss()
                            mProfileDetailsViewModel.deletePost(mShowLoader = true, postRef = postInfo._id)

                            post = postInfo
                        }
                    }
                    tvCancel.setOnClickListener {
                        this.dismiss()
                    }
                }
            }
            ValueMapping.onCommentsOrRepliesClick() -> {
                ApplicationGlobal.muteVideo = true
                mAdapter.pauseVideo()
                performFragTransaction(
                    CommentsListFragment.newInstance(postInfo._id), CommentsListFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
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
            ValueMapping.onLikesClick() -> {
                performFragTransaction(LikesFragment.newInstance(postInfo._id, true), LikesFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
        }
    }

    override fun performDetailsClickAction(user: UserProfile, follow: Boolean, clickType: Int) {
        when (clickType) {
            ValueMapping.onFollowersClick() -> {
                performFragTransaction(FollowersFollowingTabFragment.newInstance(user, true), FollowersFollowingTabFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in,
                        popExitAnim = R.anim.slide_out_right
                )
            }
            ValueMapping.onFollowingClick() -> {
                performFragTransaction(
                        FollowersFollowingTabFragment.newInstance(user, false),
                        FollowersFollowingTabFragment.TAG,
                        enterAnim = R.anim.slide_in_right,
                        exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in,
                        popExitAnim = R.anim.slide_out_right
                )
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

    fun showShimmer() {
        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        // Fetch details
        mProfileDetailsViewModel.fetchUserDetails(false, mPage = page, mResultSize = resultSize)
    }

    override fun onMuteUnmuteClicked(post: HomeFeed) {
        mAdapter.muteStatusChanged(post)
    }

    override fun onPause() {
        super.onPause()
        ApplicationGlobal.muteVideo = true
        mAdapter.pauseVideo()
    }

    override fun onResume() {
        super.onResume()
        showShimmer()
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}