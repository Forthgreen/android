package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.services.MyFirebaseMessagingService
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.HomeFeedViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.PostsFeedAdapter
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.setVisibility
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import com.forthgreen.app.workers.CreatePostWorker
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_base_recycler_view.*
import kotlinx.android.synthetic.main.fragment_posts_feed.*
import kotlinx.android.synthetic.main.fragment_posts_feed.recyclerView
import kotlinx.android.synthetic.main.fragment_posts_feed.swipeRefreshLayout

/**
 * @author Nandita Gandhi
 * @since 08-04-2021
 */
class PostsFeedFragment : BaseRecyclerViewFragment(), LoadMoreListener, PostsFeedAdapter.PostsClickCallback {

    companion object {
        const val TAG = "PostsFeedFragment"
        private const val resultSize = 10
        const val LOCAL_INTENT_ACTION_PERFORMED = "LOCAL_INTENT_ACTION_PERFORMED"
        const val LOCAL_INTENT_ACTION_MUTE = "LOCAL_INTENT_ACTION_MUTE"
        const val LOCAL_INTENT_SCROLL_TO_TOP = "LOCAT_INTENT_SCROLL_TO_TOP"
    }

    // Variables
    private var page = 1

    private val mHomeFeedViewModel by lazy {
        // Get ViewModel
        ViewModelProvider(this).get(HomeFeedViewModel::class.java)
    }
    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val mLocalBroadcastManagerLikeAndDislike by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val mAdapter by lazy { PostsFeedAdapter(this, this) }

    private var user = HomeFeed()

    private var post = HomeFeed()

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    private val selfId: String
        get() = mHomeFeedViewModel.getUserProfileDataFromSharedPrefs()._id

    override val layoutId: Int
        get() = R.layout.fragment_posts_feed

    override val viewModel: BaseViewModel?
        get() = mHomeFeedViewModel

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
        // Setup toolbar
        toolbar.navigationIcon = null
        tvToolbarTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_forthgreen_home_feed, 0, 0, 0)

        // Push Down anim
        PushDownAnim.setPushDownAnimTo(ivCreatePost, ivSearchUsers)

        // Set up Linear Layout Manager
        mLinearLayoutManager = LinearLayoutManager(requireContext())

        // Start shimmer
        flShimmerPosts.startShimmer()
        flShimmerPosts.visible()
        swipeRefreshLayout.gone()
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            toolbar.visible()
        }

        // Fetch Feed Data
        mHomeFeedViewModel.fetchFeedData(false, page, resultSize)

        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            // Fetch notifications
            mHomeFeedViewModel.fetchNotifications(false, page, resultSize)
        }

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(EditProfileFragment.LOCAL_INTENT_PROFILE_UPDATED)
                    addAction(CommentsListFragment.LOCAL_INTENT_COMMENTS_ACTION)
                    addAction(PostDetailsFragment.LOCAL_INTENT_COMMENTS_ACTION)
                    addAction(SelfProfileFragment.LOCAL_INTENT_SELF_POST_ACTION_PERFORMED)
                    addAction(OtherUserProfileFragment.LOCAL_INTENT_USER_POST_ACTION_PERFORMED)
                    addAction(NotificationDetailsFragment.LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
                    addAction(NotificationPostDetailsFragment.LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
                    addAction(CreatePostWorker.LOCAL_INTENT_POST_CREATED)
                    addAction(RepliesFragment.LOCAL_INTENT_REPLY_COMMENTS_ACTION)
                    addAction(NotificationsFragment.LOCAL_INTENT_NOTIFICATION_SEEN)
                    addAction(CreatePostWorker.LOCAL_INTENT_UPLOAD_STATUS)
                    addAction(MyFirebaseMessagingService.LOCAL_INTENT_PUSH_RECEIVED_SHOW_DOT)
                    addAction(LOCAL_INTENT_ACTION_MUTE)
                })
    }

    private fun setupListeners() {
        ivSearchUsers.setOnClickListener {
            ApplicationGlobal.muteVideo = true
            mAdapter.pauseVideo()
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
              //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
                performTransaction(SearchUsersFragment(), SearchUsersFragment.TAG)
            }
        }
        ivCreatePost.setOnClickListener {
            ApplicationGlobal.muteVideo = true
            mAdapter.pauseVideo()
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
               // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
                performFragTransaction(CreatePostFragment(), CreatePostFragment.TAG,
                    enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down)
            }
        }
        ivNotifications.setOnClickListener {
            ApplicationGlobal.muteVideo = true
            mAdapter.pauseVideo()
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
               // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
//                mHomeFeedViewModel.saveUserDetailsForNotifDot(mHomeFeedViewModel.getUserProfileDataFromSharedPrefs()
//                    .copy(showNotifyDot = false))
//                ApplicationGlobal.showNotificationDot = false
                mHomeFeedViewModel.updateNotifStatus(false)
                ivNotifDot.setVisibility(false)
                performFragTransaction(
                    NotificationsFragment(), NotificationsFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
            }
        }
    }

    private fun observeProperties() {
        mHomeFeedViewModel.onSuccessFulDataFetched().observe(viewLifecycleOwner, { fetchedData ->
            // Stop shimmer
            flShimmerPosts.stopShimmer()
            flShimmerPosts.gone()
            swipeRefreshLayout.visible()

            if (fetchedData.data.isEmpty() && page == 1) {
                mAdapter.submitList(emptyList(), false, page, selfId)
                showNoDataText(R.string.no_data_to_show)
            } else {
                hideNoDataText()
                mAdapter.submitList(fetchedData.data, fetchedData.hasMore, page, selfId)
            }
        })
        mHomeFeedViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, { statusUpdated ->
            if (statusUpdated) {
                mAdapter.updateFeedItem(user)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
        mHomeFeedViewModel.onPostDeleted().observe(viewLifecycleOwner, { deleted ->
            if (deleted) {
                mAdapter.removeFeedItem(post)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
        mHomeFeedViewModel.onPostReported().observe(viewLifecycleOwner, { reported ->
            if (reported) {
                performTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })
        mHomeFeedViewModel.onPostLikeUpdated().observe(viewLifecycleOwner, { postLikeUpdated ->
            if (postLikeUpdated) {
                mAdapter.updateFeedItem(post)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
        mHomeFeedViewModel.onNotificationsFetched().observe(viewLifecycleOwner,
            { fetchedNotifications ->
                if (fetchedNotifications.hasUnreadNotifs && ApplicationGlobal.showNotificationDot) {
                    ivNotifDot.setVisibility(true)
                } else {
                    ivNotifDot.setVisibility(false)
                }
            })
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                if (intent.action == NotificationsFragment.LOCAL_INTENT_NOTIFICATION_SEEN) {
                    // Fetch notifications
                    mHomeFeedViewModel.fetchNotifications(false, page, resultSize)
                } else if (intent.hasExtra(CreatePostWorker.BUNDLE_EXTRAS_STATUS)) {
                    val status =
                        intent.getBooleanExtra(CreatePostWorker.BUNDLE_EXTRAS_STATUS, false)
                    if (status) {
                        setupProgressBar()
                    } else {
                        pbVideoUpload.progress = 99
                        activity?.runOnUiThread {
                            Handler(Looper.getMainLooper()).postDelayed({
                                pbVideoUpload.gone()
                            }, 1000)
                        }
                    }
                } else if (intent.action == MyFirebaseMessagingService.LOCAL_INTENT_PUSH_RECEIVED_SHOW_DOT) {
                    ivNotifDot.setVisibility(true)
                } else if (intent.action == LOCAL_INTENT_ACTION_MUTE){
                    ApplicationGlobal.muteVideo = true
                    mAdapter.pauseVideo()
                } else if(intent.action == LOCAL_INTENT_SCROLL_TO_TOP){
                    scrollToTop()
                } else {
                    onPullDownToRefresh()
                }
            }
        }
    }

    private fun setupProgressBar() {
        var i = pbVideoUpload.progress
        pbVideoUpload.visible()
        // this loop will run until the value of i becomes 90
        Thread(Runnable {
            while (i < 85) {
                i += 1
                // Update the progress bar and display the current value
                Handler(Looper.getMainLooper()).post(Runnable {
                    pbVideoUpload.progress = i
                })
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    override fun onPullDownToRefresh() {
        page = 1
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()
        mHomeFeedViewModel.fetchFeedData(false, page, resultSize)
    }

    override fun onLoadMore() {
        page++
        mHomeFeedViewModel.fetchFeedData(false, page, resultSize)
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
        flShimmerPosts.startShimmer()
        flShimmerPosts.visible()
        swipeRefreshLayout.gone()

        // Fetch Feed Data
        mHomeFeedViewModel.fetchFeedData(false, page, resultSize)
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }

    override fun performPostClickAction(clickType: Int, postData: HomeFeed, postLiked: Boolean) {
        when (clickType) {
            ValueMapping.onMenuClick() -> {
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                  //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                    callUserLoginDialog()
                } else {
                    // Open Custom Dialog
                    MaterialDialog(requireContext()).show {
                        customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                        cancelOnTouchOutside(false)
                        cancelable(false)
                        cornerRadius(res = R.dimen.dialog_corner_radius)
                        if (selfId == postData.addedBy._id) {
                            tvReportAbuse.apply {
                                text = getString(R.string.remove_post_label)
                                setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                                setOnClickListener {
                                    this@show.dismiss()
                                    mHomeFeedViewModel.deletePost(mShowLoader = true, postRef = postData._id)

                                    // Update the post data to remove via adapter
                                    post = postData
                                }
                            }
                        } else {
                            tvReportAbuse.apply {
                                text = getString(R.string.report_post_label)
                                setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                                setOnClickListener {
                                    this@show.dismiss()
                                    mHomeFeedViewModel.reportPost(mShowLoader = true, postRef = postData._id)
                                }
                            }
                        }
                        tvCancel.setOnClickListener {
                            this.dismiss()
                        }
                    }
                }
            }
            ValueMapping.onProfileClick() -> {
                ApplicationGlobal.muteVideo = true
                mAdapter.pauseVideo()
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                   // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                    callUserLoginDialog()
                } else {
                    performFragTransaction(OtherUserProfileFragment.newInstance(postData.addedBy),
                        OtherUserProfileFragment.TAG,
                        enterAnim = R.anim.slide_in_right,
                        exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in,
                        popExitAnim = R.anim.slide_out_right)
                }
            }
            ValueMapping.onCommentsOrRepliesClick() -> {
                ApplicationGlobal.muteVideo = true
                mAdapter.pauseVideo()
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                   // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                    callUserLoginDialog()
                } else {
                    // we comment code
                    /*performFragTransaction(
                            CommentsListFragment.newInstance(postData._id),
                            CommentsListFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)*/

                    performFragTransaction(
                        PostDetailsFragment.newInstance(postData._id),
                        PostDetailsFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
                }
            }
            ValueMapping.onLikeOrDislike() -> {
                // Vibrate phone
                GeneralFunctions.vibratePhone(requireContext())
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                  //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                    callUserLoginDialog()
                } else {
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(
                        PostsFeedFollowingFragment.LOCAL_INTENT_LIKE_DISLIKE_ACTION_PERFORMED
                    ))
                   /* mHomeFeedViewModel.updatePostLike(
                            mShowLoader = true,
                            postRef = postData._id,
                            liked = postLiked
                    )*/
                    mHomeFeedViewModel.updatePostLike(
                        mShowLoader = false,
                        postRef = postData._id,
                        liked = postLiked
                    )
                    post = if (postLiked) {
                        postData.copy(isLike = postLiked, likes = postData.likes + 1)
                    } else {
                        postData.copy(isLike = postLiked, likes = postData.likes - 1)
                    }
                }
            }
            ValueMapping.onLikesClick() -> {
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                   // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                    callUserLoginDialog()
                } else {
                    performFragTransaction(LikesFragment.newInstance(postData._id, true), LikesFragment.TAG,
                            enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
                }
            }
        }
    }

    override fun performUserClickAction(userData: HomeFeed, openProfile: Boolean, follow: Boolean) {
        if (openProfile) {
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
               // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
                performFragTransaction(OtherUserProfileFragment.newInstance(userData.randomUserData), OtherUserProfileFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
        } else {
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
              //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
                // Hit the API to update follow status
                mHomeFeedViewModel.updateUserFollowStatus(isShowLoading = true, status = follow, userRef = userData._id)
                user = userData.copy(isFollowed = follow)
            }
        }
    }

    override fun onTaggedUserClick(taggedUser: Users) {
        when (taggedUser.type) {
            ValueMapping.onUserTaggedAction() -> {
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                   // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                    callUserLoginDialog()
                } else {
                    performFragTransaction(OtherUserProfileFragment.newInstance(
                            UserProfile(_id = taggedUser._id, image = taggedUser.image, username = taggedUser.name)),
                            OtherUserProfileFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                    )
                }
            }

            ValueMapping.onBrandTaggedAction() -> {
                performFragTransaction(BrandDetailFragment.newInstance(
                        Brand(_id = taggedUser._id, coverImage = taggedUser.image, brandName = taggedUser.name)),
                        BrandDetailFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }

            ValueMapping.onRestaurantTaggedAction() -> {
                performFragTransaction(RestaurantDetailsFragment.newInstance(
                    Restaurant(_id = taggedUser._id,
                        thumbnail = taggedUser.image,
                        name = taggedUser.name)),
                    RestaurantDetailsFragment.TAG,
                    enterAnim = R.anim.slide_in_right,
                    exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in,
                    popExitAnim = R.anim.slide_out_right)
            }
        }
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
        scrollToTop()
        mLocalBroadcastManagerLikeAndDislike.registerReceiver(mLocalBroadcastReceiver,
            IntentFilter().apply {
                addAction(PostsFeedFollowingFragment.LOCAL_INTENT_LIKE_DISLIKE_ACTION_PERFORMED)
                addAction(LOCAL_INTENT_SCROLL_TO_TOP)
            })
    }

    override fun onDestroyView() {
        mAdapter.pauseVideo()
        mLocalBroadcastManagerLikeAndDislike.unregisterReceiver(mLocalBroadcastReceiver)
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
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
}