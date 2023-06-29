package com.forthgreen.app.views.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.repository.networkrequest.WebConstants
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.CommentsListingViewModel
import com.forthgreen.app.viewmodels.NotificationDetailsViewModel
import com.forthgreen.app.views.adapters.CommentsListAdapter
import com.forthgreen.app.views.adapters.ViewPagerAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_notification_details2.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.tvToolbarTitle
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.fragment_notification_details2.cbCommentLike
import kotlinx.android.synthetic.main.fragment_notification_details2.cbLike
import kotlinx.android.synthetic.main.fragment_notification_details2.cbMute
import kotlinx.android.synthetic.main.fragment_notification_details2.cbReplyLike
import kotlinx.android.synthetic.main.fragment_notification_details2.civPostUserImage
import kotlinx.android.synthetic.main.fragment_notification_details2.civReplyUserImage
import kotlinx.android.synthetic.main.fragment_notification_details2.civUserImageComments
import kotlinx.android.synthetic.main.fragment_notification_details2.dotIndicator
import kotlinx.android.synthetic.main.fragment_notification_details2.etComment
import kotlinx.android.synthetic.main.fragment_notification_details2.flShimmer
import kotlinx.android.synthetic.main.fragment_notification_details2.flShimmerComment
import kotlinx.android.synthetic.main.fragment_notification_details2.groupComment
import kotlinx.android.synthetic.main.fragment_notification_details2.groupMultiPicturesViews
import kotlinx.android.synthetic.main.fragment_notification_details2.groupReply
import kotlinx.android.synthetic.main.fragment_notification_details2.groupVideoPlayer
import kotlinx.android.synthetic.main.fragment_notification_details2.itemVideoPlayerThumbnail
import kotlinx.android.synthetic.main.fragment_notification_details2.ivCommentOptions
import kotlinx.android.synthetic.main.fragment_notification_details2.ivMessage
import kotlinx.android.synthetic.main.fragment_notification_details2.ivMessageComments
import kotlinx.android.synthetic.main.fragment_notification_details2.ivPostMenuOptions
import kotlinx.android.synthetic.main.fragment_notification_details2.ivReplyOptions
import kotlinx.android.synthetic.main.fragment_notification_details2.nestedScrollView
import kotlinx.android.synthetic.main.fragment_notification_details2.pbVideo
import kotlinx.android.synthetic.main.fragment_notification_details2.recyclerView
import kotlinx.android.synthetic.main.fragment_notification_details2.tvComment
import kotlinx.android.synthetic.main.fragment_notification_details2.tvCommentLikes
import kotlinx.android.synthetic.main.fragment_notification_details2.tvComments
import kotlinx.android.synthetic.main.fragment_notification_details2.tvLikes
import kotlinx.android.synthetic.main.fragment_notification_details2.tvLoadMoreComments
import kotlinx.android.synthetic.main.fragment_notification_details2.tvPostDescription
import kotlinx.android.synthetic.main.fragment_notification_details2.tvReplies
import kotlinx.android.synthetic.main.fragment_notification_details2.tvReply
import kotlinx.android.synthetic.main.fragment_notification_details2.tvReplyLikes
import kotlinx.android.synthetic.main.fragment_notification_details2.tvSendComment
import kotlinx.android.synthetic.main.fragment_notification_details2.tvUserFullName
import kotlinx.android.synthetic.main.fragment_notification_details2.tvUserFullNameComment
import kotlinx.android.synthetic.main.fragment_notification_details2.tvUserFullNameReply
import kotlinx.android.synthetic.main.fragment_notification_details2.tvUserName
import kotlinx.android.synthetic.main.fragment_notification_details2.tvViewMore
import kotlinx.android.synthetic.main.fragment_notification_details2.tvViewMoreComment
import kotlinx.android.synthetic.main.fragment_notification_details2.tvViewMoreReply
import kotlinx.android.synthetic.main.fragment_notification_details2.vViewVideoPlayer
import kotlinx.android.synthetic.main.fragment_notification_details2.viewPager
import kotlinx.android.synthetic.main.fragment_notification_details2.viewProfile
import kotlinx.android.synthetic.main.fragment_notification_details2.vvVideoPlay

/**
 * @author Nandita Gandhi
 * @since 03-05-2021
 */
class NotificationPostDetailsFragment : BaseRecyclerViewFragment(), View.OnClickListener,
    LoadMoreListener, CommentsListAdapter.CommentViewsClickCallbacks {

    companion object {
        const val TAG = "NotificationPostDetailsFragment"
        private const val POST_DELETED_ERROR_CODE = 302
        private const val BUNDLE_EXTRAS_NOTIFICATION_ID = "BUNDLE_EXTRAS_NOTIFICATION_ID"
        private const val BUNDLE_EXTRAS_NOTIFICATION_TYPE = "BUNDLE_EXTRAS_NOTIFICATION_TYPE"
        const val LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED = "LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED"
        const val LOCAL_INTENT_NOTIFICATION_REMOVE_ACTION = "LOCAL_INTENT_NOTIFICATION_REMOVE_ACTION"

        fun newInstance(notificationId: String, notificationType: Int): NotificationPostDetailsFragment {
            return NotificationPostDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_NOTIFICATION_ID, notificationId)
                    putInt(BUNDLE_EXTRAS_NOTIFICATION_TYPE, notificationType)
                }
            }
        }
    }

    // Variables
    private var mPage = 1

    private var mTaggedUsersList = mutableListOf<Users>()

    private val mAdapter by lazy { CommentsListAdapter(this, this) }

    private val mNotificationDetailsViewModel by lazy {
        ViewModelProvider(this).get(NotificationDetailsViewModel::class.java)
    }
    private val mCommentsListViewModel by lazy {
        // get View Model
        ViewModelProvider(this).get(CommentsListingViewModel::class.java)
    }
    private val notificationId by lazy {
        requireArguments().getString(BUNDLE_EXTRAS_NOTIFICATION_ID, "")
    }
    private val notificationType by lazy {
        requireArguments().getInt(BUNDLE_EXTRAS_NOTIFICATION_TYPE, ValueMapping.onNotifPostLiked())
    }
    private val mImageViewPagerAdapter by lazy {
        ViewPagerAdapter()
    }
    private var notificationData = PostDetails()

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private var commentClicked = false      // To check if comment is clicked or reply is clicked.
    private var commentRemoved = false     // To check if comment is removed or reply is removed.

    private var videoUriString: String? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private val selfId: String
        get() = mNotificationDetailsViewModel.getUserProfileDataFromSharedPrefs()._id

    override val layoutId: Int
        get() = R.layout.fragment_notification_details2


    override val viewModel: BaseViewModel?
        get() = mNotificationDetailsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    private var comment = Comment()


    override fun onPullDownToRefresh() {

    }

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, IntentFilter().apply {
            addAction(RepliesFragment.LOCAL_INTENT_REPLY_COMMENTS_ACTION)
            addAction(CommentsListFragment.LOCAL_INTENT_COMMENTS_ACTION)
        })
    }

    private fun setupViews() {

        // Set up toolbar title
        tvToolbarTitle.text = getString(R.string.post_toolbar_title)

        // Start Shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        flShimmerComment.startShimmer()
        flShimmerComment.visible()
        nestedScrollView.gone()

        // Set up viewPager
        viewPager.adapter = mImageViewPagerAdapter
        dotIndicator.setViewPager2(viewPager)

        // Fetch notification Details
        mNotificationDetailsViewModel.fetchNotificationDetails(false, notificationId)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        etComment.setOnTouchListener(OnTouchListener { view, motionEvent -> // your code here....
            nestedScrollView.scrollTo(0,0)
            false
        })
        etComment.doOnTextChanged { text, start, before, count ->
            if ('@' in text) {
                val pos = text.lastIndexOf('@')
                if (pos == 0 || text.get(pos - 1) == ' ') {
                    val str = text.substring(pos + 1)
                    mCommentsListViewModel.getUsersToTag(false, str)
                }
            } else {
               // recyclerViewTags.gone()
                recyclerView.visible()
              //  swiper.visible()
            }
        }
        tvSendComment.setOnClickListener {
            // Add comment
            if (etComment.text.isEmpty()) {
                // Uncomment to show error message.
                //showMessage(resId = R.string.empty_comment_message)
            } else {
                checkForTaggedUsersAndHitAPI()
                etComment.setText("")
                hideKeyboard()
            }
        }

        viewProfile.setOnClickListener(this)
        tvComments.setOnClickListener(this)
        ivMessage.setOnClickListener(this)
        cbLike.setOnClickListener(this)
        tvViewMore.setOnClickListener(this)
        civUserImageComments.setOnClickListener(this)
        tvUserFullNameComment.setOnClickListener(this)
        ivMessageComments.setOnClickListener(this)
        tvReplies.setOnClickListener(this)
        cbCommentLike.setOnClickListener(this)
        tvViewMoreComment.setOnClickListener(this)
        civReplyUserImage.setOnClickListener(this)
        tvUserFullNameReply.setOnClickListener(this)
        cbReplyLike.setOnClickListener(this)
        tvViewMoreReply.setOnClickListener(this)
        tvLoadMoreComments.setOnClickListener(this)
        ivPostMenuOptions.setOnClickListener(this)
        ivCommentOptions.setOnClickListener(this)
        ivReplyOptions.setOnClickListener(this)
        tvLikes.setOnClickListener(this)
        tvCommentLikes.setOnClickListener(this)
        tvReplyLikes.setOnClickListener(this)
        vViewVideoPlayer.setOnClickListener {
            if (mediaPlayer != null && itemVideoPlayerThumbnail.visibility == View.GONE) {
                if (cbMute.isChecked) {
                    mediaPlayer.setVolume(0f, 0f)
                } else {
                    mediaPlayer.setVolume(1f, 1f)
                }
                cbMute.isChecked = !cbMute.isChecked
            }
        }
    }

    private fun observeProperties() {

        mCommentsListViewModel.onCommentAdded().observe(viewLifecycleOwner, { addedComment ->
            hideNoDataText()
           // mAdapter.addComment(addedComment.data)
            mAdapter.addCommentFirstPostion(addedComment.data)
            // Send Broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(Intent(CommentsListFragment.LOCAL_INTENT_COMMENTS_ACTION))
        })
        mCommentsListViewModel.onCommentLikeUpdated().observe(viewLifecycleOwner, { likeUpdated ->
            if (likeUpdated) {
                mAdapter.updateComment(comment)
            }
        })
        mCommentsListViewModel.onCommentDeleted().observe(viewLifecycleOwner, { deleteSuccess ->
            if (deleteSuccess) {
                mAdapter.removeComment(comment)
                if (mAdapter.itemCount == 0) {
                    showNoDataText(resId = R.string.no_data_to_show)
                }
                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(CommentsListFragment.LOCAL_INTENT_COMMENTS_ACTION))
            }
        })
        mCommentsListViewModel.onCommentReported().observe(viewLifecycleOwner, { reportSuccess ->
            if (reportSuccess) {
                performFragTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })

        mNotificationDetailsViewModel.onCommentsFetched().observe(viewLifecycleOwner, { commentsFetched ->
            // Hide Shimmer
//            flShimmer.stopShimmer()
//            flShimmer.gone()
//            swipeRefreshLayout.visible()
          //  groupAddComment.visible()

            flShimmerComment.stopShimmer()
            flShimmerComment.gone()

            if (commentsFetched.data.isEmpty() && mPage == 1) {
                mAdapter.submitList(emptyList(), false, mPage, selfId)
                showNoDataText(resId = R.string.no_comments_label)
            } else {
                hideNoDataText()
                mAdapter.submitList(commentsFetched.data, commentsFetched.hasMore, mPage, selfId)
                if (!commentsFetched.hasMore && mAdapter.commentList.size>10) {
                    tv_transparent.visible()
                } else {
                    tv_transparent.gone()
                }
            }
        })

        mNotificationDetailsViewModel.onNotificationDetailsFetched().observe(viewLifecycleOwner, { detailsFetched ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            nestedScrollView.visible()
          //  svDetails.gone()

            if (detailsFetched.code == POST_DELETED_ERROR_CODE) {
                when (notificationType) {
                    ValueMapping.onNotifPostLiked(), ValueMapping.onNotifTaggedPost() -> {
                        showMessage(resId = R.string.post_deleted_message)
                    }
                    ValueMapping.onNotifComment(), ValueMapping.onNotifCommentLiked(), ValueMapping.onNotifTaggedComment() -> {
                        showMessage(resId = R.string.comment_deleted_message)
                    }
                    ValueMapping.onNotifReply(), ValueMapping.onNotifReplyLiked() -> {
                        showMessage(resId = R.string.reply_deleted_message)
                    }
                }
                supportFragmentManager().popBackStack()
            } else {
                // Update the views with data fetched
                updateViews(detailsFetched.data.first())
                notificationData = detailsFetched.data.first()

                // Fetch Comment List
                mNotificationDetailsViewModel.fetchCommentsList(
                    mShowLoader = false,
                    page = mPage,
                    postRef = notificationData.posts._id
                )
            }
        })

        mNotificationDetailsViewModel.onPostReported().observe(viewLifecycleOwner, { postReported ->
            if (postReported) {
                performFragTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })
        mNotificationDetailsViewModel.onPostLikeUpdated().observe(viewLifecycleOwner, { postLikeUpdated ->
            if (postLikeUpdated) {
                cbLike.isChecked = !cbLike.isChecked
                if (cbLike.isChecked) {
                    // Update Global Notification Data Model
                    notificationData.posts.apply {
                        val posts = this.copy(likes = this.likes + 1, isLike = true)
                        notificationData = notificationData.copy(posts = posts)
                        tvLikes.text = posts.likesText
                    }
                } else {
                    // Update Global Notification Data Model
                    notificationData.posts.apply {
                        val posts = this.copy(likes = this.likes - 1, isLike = false)
                        notificationData = notificationData.copy(posts = posts)
                        tvLikes.text = posts.likesText
                    }
                }
                // Send broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
                )
            }
        })
        mNotificationDetailsViewModel.onPostDeleted().observe(viewLifecycleOwner, { postDeleted ->
            if (postDeleted) {
                supportFragmentManager().popBackStack()

                // Send broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
                )
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_REMOVE_ACTION)
                )
            }
        })
        mNotificationDetailsViewModel.onCommentReported().observe(viewLifecycleOwner, { commentReported ->
            if (commentReported) {
                performFragTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })
        mNotificationDetailsViewModel.onCommentLikeUpdated().observe(viewLifecycleOwner, { commentLikeUpdated ->
            if (commentLikeUpdated) {
                if (commentClicked) {
                    cbCommentLike.isChecked = !cbCommentLike.isChecked
                    if (cbCommentLike.isChecked) {
                        // Update Global Notification Data Model
                        notificationData.posts.comment.apply {
                            val comment = this.copy(likes = this.likes + 1, isLike = true)
                            val post = notificationData.posts.copy(comment = comment)
                            notificationData = notificationData.copy(posts = post)
                            tvCommentLikes.text = comment.likesText
                        }
                    } else {
                        // Update Global Notification Data Model
                        notificationData.posts.comment.apply {
                            val comment = this.copy(likes = this.likes - 1, isLike = false)
                            val post = notificationData.posts.copy(comment = comment)
                            notificationData = notificationData.copy(posts = post)
                            tvCommentLikes.text = comment.likesText
                        }
                    }
                } else {
                    cbReplyLike.isChecked = !cbReplyLike.isChecked
                    if (cbReplyLike.isChecked) {
                        // Update Global Notification Data Model
                        notificationData.posts.comment.reply.apply {
                            val reply = this.copy(likes = this.likes + 1, isLike = true)
                            val comment = notificationData.posts.comment.copy(reply = reply)
                            val post = notificationData.posts.copy(comment = comment)
                            notificationData = notificationData.copy(posts = post)
                            tvReplyLikes.text = reply.likesText
                        }
                    } else {
                        // Update Global Notification Data Model
                        notificationData.posts.comment.reply.apply {
                            val reply = this.copy(likes = this.likes - 1, isLike = false)
                            val comment = notificationData.posts.comment.copy(reply = reply)
                            val post = notificationData.posts.copy(comment = comment)
                            notificationData = notificationData.copy(posts = post)
                            tvReplyLikes.text = reply.likesText
                        }
                    }
                }

                // Send broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
                )
            }
        })
        mNotificationDetailsViewModel.onCommentDeleted().observe(viewLifecycleOwner, { commentDeleted ->
            if (commentDeleted) {
                // Update Ref Type value of notification and reset views.
                val notificationType = if (commentRemoved) {
                    // If comment is removed, show views as if it is a post type notification.
                    ValueMapping.onNotifPostLiked()
                } else {
                    // If reply is removed, show views as if it is a comment type notification.
                    ValueMapping.onNotifComment()
                }

                notificationData = notificationData.copy(refType = notificationType)
                updateViews(notificationData)

                // Send broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_POST_ACTION_PERFORMED)
                )
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_REMOVE_ACTION)
                )
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.viewProfile -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0f, 0f)
                    cbMute.isChecked = false
                }
                performFragTransaction(OtherUserProfileFragment.newInstance(user = notificationData.posts.addedBy),
                    OtherUserProfileFragment.TAG)
            }
            R.id.tvComments, R.id.ivMessage -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0f, 0f)
                    cbMute.isChecked = false
                }
                nestedScrollView.fullScroll(
                    View.FOCUS_DOWN
                )

                // we comment code
               /* performFragTransaction(
                    CommentsListFragment.newInstance(mPostRef = notificationData.posts._id),
                    CommentsListFragment.TAG,
                    enterAnim = R.anim.slide_in_right,
                    exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in,
                    popExitAnim = R.anim.slide_out_right)*/
            }
            R.id.cbLike -> {
                GeneralFunctions.vibratePhone(requireContext())
              /*  mNotificationDetailsViewModel.updatePostLike(
                        mShowLoader = true,
                        postRef = notificationData.posts._id, liked = cbLike.isChecked
                )*/
                mNotificationDetailsViewModel.updatePostLike(
                    mShowLoader = false,
                    postRef = notificationData.posts._id, liked = cbLike.isChecked
                )
                // Invert the status till API hit
                cbLike.isChecked = !cbLike.isChecked
            }
            R.id.civUserImageComments, R.id.tvUserFullNameComment -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0f, 0f)
                    cbMute.isChecked = false
                }
                performFragTransaction(OtherUserProfileFragment.newInstance(user = notificationData.posts.comment.addedBy),
                    OtherUserProfileFragment.TAG)
            }
            R.id.tvReplies, R.id.ivMessageComments -> {
                performFragTransaction(
                        RepliesFragment.newInstance(mPostRef = notificationData.posts._id,
                                mComment = notificationData.posts.comment.postCommentData),
                        RepliesFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
            R.id.cbCommentLike -> {
                commentClicked = true
                mNotificationDetailsViewModel.updateCommentLike(mShowLoader = true,
                        commentRef = notificationData.posts.comment._id, liked = cbCommentLike.isChecked)
                // Invert the status till API hit
                cbCommentLike.isChecked = !cbCommentLike.isChecked
            }
            R.id.civReplyUserImage, R.id.tvUserFullNameReply -> {
                performFragTransaction(OtherUserProfileFragment.newInstance(user = notificationData.posts.comment.reply.addedBy),
                        OtherUserProfileFragment.TAG)
            }
            R.id.cbReplyLike -> {
                commentClicked = false
                mNotificationDetailsViewModel.updateCommentLike(mShowLoader = true,
                        commentRef = notificationData.posts.comment.reply._id, liked = cbReplyLike.isChecked)
                // Invert the status till API hit
                cbReplyLike.isChecked = !cbReplyLike.isChecked
            }
            R.id.tvViewMore -> {
                tvPostDescription.maxLines = Int.MAX_VALUE
                tvViewMore.gone()
            }
            R.id.tvViewMoreComment -> {
                tvComment.maxLines = Int.MAX_VALUE
                tvViewMoreComment.gone()
            }
            R.id.tvViewMoreReply -> {
                tvReply.maxLines = Int.MAX_VALUE
                tvViewMoreReply.gone()
            }
            R.id.tvLoadMoreComments -> {
                /*when (notificationData.refType) {
                    ValueMapping.onNotifComment(), ValueMapping.onNotifCommentLiked() -> {
                        performFragTransaction(CommentsListFragment.newInstance(mPostRef = notificationData.posts._id),
                                CommentsListFragment.TAG)
                    }
                    ValueMapping.onNotifReply(), ValueMapping.onNotifReplyLiked() -> {
                        performFragTransaction(RepliesFragment.newInstance(mPostRef = notificationData.posts._id,
                                mComment = notificationData.posts.comment.postCommentData), RepliesFragment.TAG)
                    }
                }*/
                // Load Comment Listing Fragment
                performFragTransaction(CommentsListFragment.newInstance(
                        mPostRef = notificationData.posts._id), CommentsListFragment.TAG)
            }
            R.id.ivPostMenuOptions -> {
                openDialog(menuClick = ValueMapping.onPostMenuClick())
            }
            R.id.ivCommentOptions -> {
                openDialog(menuClick = ValueMapping.onCommentMenuClick())
            }
            R.id.ivReplyOptions -> {
                openDialog(menuClick = ValueMapping.onReplyMenuClick())
            }
            R.id.tvLikes -> {
                performFragTransaction(LikesFragment.newInstance(notificationData.posts._id, true), LikesFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
            R.id.tvCommentLikes -> {
                performFragTransaction(LikesFragment.newInstance(notificationData.posts.comment._id),
                        LikesFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
            R.id.tvReplyLikes -> {
                performFragTransaction(LikesFragment.newInstance(notificationData.posts.comment.reply._id),
                        LikesFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
        }
    }

    private fun updateViews(postDetails: PostDetails) {
        when (postDetails.refType) {
            ValueMapping.onNotifPostLiked(), ValueMapping.onNotifTaggedPost() -> {
                groupComment.gone()
                groupReply.gone()
            }
            ValueMapping.onNotifComment(), ValueMapping.onNotifCommentLiked(), ValueMapping.onNotifTaggedComment() -> {
                groupComment.gone()
              //  groupComment.visible()
                groupReply.gone()
            }
            ValueMapping.onNotifReply(), ValueMapping.onNotifReplyLiked() -> {
                groupComment.visible()
                groupReply.visible()
            }
        }

        // Update post views
        postDetails.posts.apply {
            if (addedBy.dummyUser && selfId != addedBy._id) {
                ivPostMenuOptions.gone()
            } else {
                ivPostMenuOptions.visible()
            }
            when (type) {
                ValueMapping.onPostWithImageOnly() -> {
                    groupMultiPicturesViews.visible()
                    tvPostDescription.gone()
                    mImageViewPagerAdapter.submitList(image)
                    groupVideoPlayer.gone()
                    itemVideoPlayerThumbnail.gone()
                    pbVideo.gone()
                }
                ValueMapping.onPostWithImageNText() -> {
                    groupMultiPicturesViews.visible()
                    tvPostDescription.visible()
                    mImageViewPagerAdapter.submitList(image)
                    groupVideoPlayer.gone()
                    itemVideoPlayerThumbnail.gone()
                    pbVideo.gone()
                }
                ValueMapping.onPostWithTextOnly() -> {
                    groupMultiPicturesViews.gone()
                    tvPostDescription.visible()
                    groupVideoPlayer.gone()
                    itemVideoPlayerThumbnail.gone()
                    pbVideo.gone()
                }
                ValueMapping.onPostWithVideoOnly() -> {
                    val params = vvVideoPlay.layoutParams
                    if (videoHeight == 0 && videoWidth == 0) {
                        (params as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
                    } else {
                        val ratio = (videoWidth.toDouble() / videoHeight.toDouble())
                        val dimen = String.format("%.1f", ratio)
                        (params as ConstraintLayout.LayoutParams).dimensionRatio = dimen
                    }
                    vvVideoPlay.layoutParams = params
                    groupMultiPicturesViews.gone()
                    tvPostDescription.gone()
                    groupVideoPlayer.visible()
                    itemVideoPlayerThumbnail.visible()
                    itemVideoPlayerThumbnail.loadURL(thumbnail, false)
                    pbVideo.visible()
                    videoUriString = WebConstants.fetchVideoURL(video)
                    vvVideoPlay.setVideoPath(videoUriString)
                    vvVideoPlay.requestFocus()
                    vvVideoPlay.start()
                    vvVideoPlay.setOnPreparedListener(OnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                        mediaPlayer = mp
                    })
                    vvVideoPlay.setOnInfoListener(MediaPlayer.OnInfoListener { mediaPlayer, i, i1 ->
                        if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            pbVideo.gone()
                            itemVideoPlayerThumbnail.gone()
//                            first frame was bufered - do your stuff here
//                            activity?.runOnUiThread {
//                                Handler(Looper.getMainLooper()).postDelayed({
//                                    pbVideo.gone()
//                                    itemVideoPlayerThumbnail.gone()
//                                }, 6000)
//                            }
                        }
                        false
                    })
                }
                ValueMapping.onPostWithVideoNText() -> {
                    val params = vvVideoPlay.layoutParams
                    if (videoHeight == 0 && videoWidth == 0) {
                        (params as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
                    } else {
                        val ratio = (videoWidth.toDouble() / videoHeight.toDouble())
                        val dimen = String.format("%.1f", ratio)
                        (params as ConstraintLayout.LayoutParams).dimensionRatio = dimen
                    }
                    vvVideoPlay.layoutParams = params
                    groupVideoPlayer.visible()
                    pbVideo.visible()
                    itemVideoPlayerThumbnail.visible()
                    itemVideoPlayerThumbnail.loadURL(thumbnail, false)
                    groupMultiPicturesViews.gone()
                    tvPostDescription.visible()
                    videoUriString = WebConstants.fetchVideoURL(video)
                    vvVideoPlay.setVideoPath(videoUriString)
                    vvVideoPlay.requestFocus()
                    vvVideoPlay.start()
                    vvVideoPlay.setOnPreparedListener(OnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                        mediaPlayer = mp
                    })
                    vvVideoPlay.setOnInfoListener(MediaPlayer.OnInfoListener { mediaPlayer, i, i1 ->
                        if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            pbVideo.gone()
                            itemVideoPlayerThumbnail.gone()
//                            first frame was bufered - do your stuff here
//                            activity?.runOnUiThread {
//                                Handler(Looper.getMainLooper()).postDelayed({
//                                    pbVideo.gone()
//                                    itemVideoPlayerThumbnail.gone()
//                                }, 6000)
//                            }
                        }
                        false
                    })
                }
                else -> {
                    groupVideoPlayer.gone()
                    groupMultiPicturesViews.gone()
                    tvPostDescription.gone()
                    pbVideo.gone()
                    itemVideoPlayerThumbnail.gone()
                }
            }
            tvPostDescription.text = text
            if (tags.isNotEmpty()) {
                tags.forEach { tag ->
                    if (text.contains(tag.name)) {
                        tvPostDescription.makeTextLink(tag.name, underlined = false,
                                bold = false, color = R.color.colorTaggedUser,
                                action = {
                                    when (tag.type) {
                                        ValueMapping.onUserTaggedAction() -> {
                                            performFragTransaction(
                                                    OtherUserProfileFragment.newInstance(
                                                            UserProfile(_id = tag._id, image = tag.image,
                                                                    username = tag.name)), OtherUserProfileFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                        ValueMapping.onBrandTaggedAction() -> {
                                            performFragTransaction(
                                                    BrandDetailFragment.newInstance(
                                                            Brand(_id = tag._id, coverImage = tag.image,
                                                                    brandName = tag.name)), BrandDetailFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                        ValueMapping.onRestaurantTaggedAction() -> {
                                            performFragTransaction(RestaurantDetailsFragment.newInstance(
                                                    Restaurant(_id = tag._id, thumbnail = tag.image,
                                                            name = tag.name)), RestaurantDetailsFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                    }
                                })
                    }
                }
            }
            tvPostDescription.maxLines = Constants.MAX_LINES_LIMIT
            civPostUserImage.loadURL(addedBy.image, true)
            tvUserFullName.text = addedBy.firstName
            tvUserName.text = addedBy.usernameText
            tvComments.text = commentsText
            tvLikes.text = likesText
            cbLike.isChecked = isLike

            // Make total likes and comments as bold.
            tvComments.makeTextLink("$comments", underlined = false, bold = true)
            tvLikes.makeTextLink("$likes", underlined = false, bold = true)

            // Add Utility to set ViewMore According to line count.
            tvPostDescription.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    try {
                        tvPostDescription.viewTreeObserver.removeOnPreDrawListener(this)
                        /*
                         * Check if text assigned to the view is of length greater than its maxLines,
                         * then show viewMore view; else hide it.
                         */
                        if (tvPostDescription.lineCount > Constants.MAX_LINES_LIMIT &&
                                tvPostDescription.maxLines <= Constants.MAX_LINES_LIMIT) {
                            tvViewMore.visible()
                        } else {
                            tvViewMore.gone()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
            })
        }

        // Update comment views
        postDetails.posts.comment.apply {
            if (addedBy.dummyUser && selfId != addedBy._id) {
                ivCommentOptions.gone()
            } else {
                ivCommentOptions.visible()
            }
            civUserImageComments.loadURL(addedBy.image, true)
            tvUserFullNameComment.text = addedBy.firstName
            tvComment.text = comment
            if (tags.isNotEmpty()) {
                tags.forEach { tag ->
                    if (comment.contains(tag.name)) {
                        tvComment.makeTextLink(tag.name, underlined = false,
                                bold = false, color = R.color.colorTaggedUser,
                                action = {
                                    when (tag.type) {
                                        ValueMapping.onUserTaggedAction() -> {
                                            performFragTransaction(
                                                    OtherUserProfileFragment.newInstance(
                                                            UserProfile(_id = tag._id, image = tag.image,
                                                                    username = tag.name)), OtherUserProfileFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                        ValueMapping.onBrandTaggedAction() -> {
                                            performFragTransaction(
                                                    BrandDetailFragment.newInstance(
                                                            Brand(_id = tag._id, coverImage = tag.image,
                                                                    brandName = tag.name)), BrandDetailFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                        ValueMapping.onRestaurantTaggedAction() -> {
                                            performFragTransaction(RestaurantDetailsFragment.newInstance(
                                                    Restaurant(_id = tag._id, thumbnail = tag.image,
                                                            name = tag.name)), RestaurantDetailsFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                    }
                                })
                    }
                }
            }
            tvComment.maxLines = Constants.MAX_LINES_LIMIT
            tvReplies.text = repliesText
            tvCommentLikes.text = likesText
            cbCommentLike.isChecked = isLike

            // Make total likes and replies as bold.
            tvReplies.makeTextLink("$replies", underlined = false, bold = true)
            tvCommentLikes.makeTextLink("$likes", underlined = false, bold = true)

            // Add Utility to set ViewMore According to line count.
            tvComment.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    try {
                        tvComment.viewTreeObserver.removeOnPreDrawListener(this)
                        /*
                         * Check if text assigned to the view is of length greater than its maxLines,
                         * then show viewMore view; else hide it.
                         */
                        if (tvComment.lineCount > Constants.MAX_LINES_LIMIT &&
                                tvComment.maxLines <= Constants.MAX_LINES_LIMIT) {
                            tvViewMoreComment.visible()
                        } else {
                            tvViewMoreComment.gone()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
            })
        }

        // Update reply views
        postDetails.posts.comment.reply.apply {
            if (addedBy.dummyUser && selfId != addedBy._id) {
                ivReplyOptions.gone()
            } else {
                ivReplyOptions.visible()
            }
            civReplyUserImage.loadURL(addedBy.image, true)
            tvUserFullNameReply.text = addedBy.firstName
            tvReply.text = comment
            if (tags.isNotEmpty()) {
                tags.forEach { tag ->
                    if (comment.contains(tag.name)) {
                        tvReply.makeTextLink(tag.name, underlined = false,
                                bold = false, color = R.color.colorTaggedUser,
                                action = {
                                    when (tag.type) {
                                        ValueMapping.onUserTaggedAction() -> {
                                            performFragTransaction(
                                                    OtherUserProfileFragment.newInstance(
                                                            UserProfile(_id = tag._id, image = tag.image,
                                                                    username = tag.name)), OtherUserProfileFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                        ValueMapping.onBrandTaggedAction() -> {
                                            performFragTransaction(
                                                    BrandDetailFragment.newInstance(
                                                            Brand(_id = tag._id, coverImage = tag.image,
                                                                    brandName = tag.name)), BrandDetailFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                        ValueMapping.onRestaurantTaggedAction() -> {
                                            performFragTransaction(RestaurantDetailsFragment.newInstance(
                                                    Restaurant(_id = tag._id, thumbnail = tag.image,
                                                            name = tag.name)), RestaurantDetailsFragment.TAG,
                                                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                                                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                                            )
                                        }
                                    }
                                })
                    }
                }
            }
            tvReply.maxLines = Constants.MAX_LINES_LIMIT
            cbReplyLike.isChecked = isLike
            tvReplyLikes.text = likesText

            // Make total likes as bold.
            tvReplyLikes.makeTextLink("$likes", underlined = false, bold = true)

            // Add Utility to set ViewMore According to line count.
            tvReply.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    try {
                        tvReply.viewTreeObserver.removeOnPreDrawListener(this)
                        /*
                         * Check if text assigned to the view is of length greater than its maxLines,
                         * then show viewMore view; else hide it.
                         */
                        if (tvReply.lineCount > Constants.MAX_LINES_LIMIT &&
                                tvReply.maxLines <= Constants.MAX_LINES_LIMIT) {
                            tvViewMoreReply.visible()
                        } else {
                            tvViewMoreReply.gone()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
            })
        }
    }

    private fun openDialog(menuClick: Int) {
        when (menuClick) {
            ValueMapping.onPostMenuClick() -> {
                // Open Custom Dialog
                MaterialDialog(requireContext()).show {
                    customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                    cancelOnTouchOutside(false)
                    cancelable(false)
                    cornerRadius(res = R.dimen.dialog_corner_radius)
                    if (selfId == notificationData.posts.addedBy._id) {
                        tvReportAbuse.apply {
                            text = getString(R.string.remove_post_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                // Remove self post
                                mNotificationDetailsViewModel.deletePost(true, notificationData.posts._id)
                            }
                        }
                    } else {
                        tvReportAbuse.apply {
                            text = getString(R.string.report_post_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                // Report Post
                                mNotificationDetailsViewModel.reportPost(true, notificationData.posts._id)
                            }
                        }
                    }
                    tvCancel.setOnClickListener {
                        this.dismiss()
                    }
                }
            }
            ValueMapping.onCommentMenuClick() -> {
                // Open Custom Dialog
                MaterialDialog(requireContext()).show {
                    customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                    cancelOnTouchOutside(false)
                    cancelable(false)
                    cornerRadius(res = R.dimen.dialog_corner_radius)
                    if (selfId == notificationData.posts.comment.addedBy._id) {
                        tvReportAbuse.apply {
                            text = getString(R.string.remove_comment_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                // Remove self comment
                                commentRemoved = true
                                mNotificationDetailsViewModel.deleteComment(true, notificationData.posts.comment._id)
                            }
                        }
                    } else {
                        tvReportAbuse.apply {
                            text = getString(R.string.report_abuse)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorReportAbuse))
                            setOnClickListener {
                                this@show.dismiss()
                                // Report Comment
                                mNotificationDetailsViewModel.reportComment(true, notificationData.posts.comment._id)
                            }
                        }
                    }
                    tvCancel.setOnClickListener {
                        this.dismiss()
                    }
                }
            }
            ValueMapping.onReplyMenuClick() -> {
                // Open Custom Dialog
                MaterialDialog(requireContext()).show {
                    customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                    cancelOnTouchOutside(false)
                    cancelable(false)
                    cornerRadius(res = R.dimen.dialog_corner_radius)
                    if (selfId == notificationData.posts.comment.reply.addedBy._id) {
                        tvReportAbuse.apply {
                            text = getString(R.string.remove_reply_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                // Remove self reply
                                commentRemoved = false
                                mNotificationDetailsViewModel.deleteComment(true, notificationData.posts.comment.reply._id)
                            }
                        }
                    } else {
                        tvReportAbuse.apply {
                            text = getString(R.string.report_abuse)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorReportAbuse))
                            setOnClickListener {
                                this@show.dismiss()
                                // Report reply
                                mNotificationDetailsViewModel.reportComment(true, notificationData.posts.comment.reply._id)
                            }
                        }
                    }
                    tvCancel.setOnClickListener {
                        this.dismiss()
                    }
                }
            }
        }
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
               // setupViews()
            }
        }
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }

    override fun onLoadMore() {
        mPage++
        mNotificationDetailsViewModel.fetchCommentsList(
            mShowLoader = false,
            page = mPage,
            postRef = notificationData.posts._id
        )
    }

    override fun performCommentClickAction(
        commentInfo: Comment,
        clickType: Int,
        commentLiked: Boolean
    ) {
        when (clickType) {
            ValueMapping.onMenuClick() -> {
                // Open Custom Dialog
                MaterialDialog(requireContext()).show {
                    customView(R.layout.dialog_review_menu, dialogWrapContent = true, noVerticalPadding = true)
                    cancelOnTouchOutside(false)
                    cancelable(false)
                    cornerRadius(res = R.dimen.dialog_corner_radius)
                    if (selfId == commentInfo.addedBy._id) {
                        tvReportAbuse.apply {
                            text = getString(R.string.remove_comment_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                comment = commentInfo
                                // Remove self comment
                                mCommentsListViewModel.deleteComment(true, commentInfo._id)
                            }
                        }
                    } else {
                        tvReportAbuse.apply {
                            text = getString(R.string.report_comment_label)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                comment = commentInfo
                                // Report comment
                                mCommentsListViewModel.reportComment(true, commentInfo._id)
                            }
                        }
                    }
                    tvCancel.setOnClickListener {
                        this.dismiss()
                    }
                }
            }
            ValueMapping.onCommentsOrRepliesClick() -> {
                performFragTransaction(
                    RepliesFragment.newInstance(notificationData.posts._id, commentInfo), RepliesFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
            }
            ValueMapping.onLikeOrDislike() -> {
                // Vibrate phone
                GeneralFunctions.vibratePhone(requireContext())
                /*mCommentsListViewModel.updateCommentLike(
                    true,
                    commentInfo._id,
                    liked = commentLiked
                )*/
                mCommentsListViewModel.updateCommentLike(
                    false,
                    commentInfo._id,
                    liked = commentLiked
                )
                comment = if (commentLiked) {
                    commentInfo.copy(isLike = commentLiked, likes = commentInfo.likes + 1)
                } else {
                    commentInfo.copy(isLike = commentLiked, likes = commentInfo.likes - 1)
                }
            }
        }
    }

    override fun openUserProfile(user: UserProfile) {
        performFragTransaction(
            OtherUserProfileFragment.newInstance(user),
            OtherUserProfileFragment.TAG
        )
    }

    override fun showUsersWhoLiked(commentInfo: Comment) {
        performFragTransaction(
            LikesFragment.newInstance(commentInfo._id), LikesFragment.TAG,
            enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
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
                ), OtherUserProfileFragment.TAG
            )

            ValueMapping.onBrandTaggedAction() -> performFragTransaction(
                BrandDetailFragment.newInstance(
                    Brand(
                        _id = taggedUser._id,
                        coverImage = taggedUser.image,
                        brandName = taggedUser.name
                    )
                ), BrandDetailFragment.TAG
            )

            ValueMapping.onRestaurantTaggedAction() -> performFragTransaction(
                RestaurantDetailsFragment.newInstance(
                    Restaurant(
                        _id = taggedUser._id,
                        thumbnail = taggedUser.image,
                        name = taggedUser.name
                    )
                ), RestaurantDetailsFragment.TAG
            )
        }
    }

    private fun checkForTaggedUsersAndHitAPI() {
        val usersToRemove = mutableListOf<Users>()
        mTaggedUsersList.forEach { user ->
            if (!(etComment.trimmedText.contains(user.name))) {
                usersToRemove.add(user)
            }
        }
        mTaggedUsersList.removeAll(usersToRemove)
        var str = etComment.text.toString()
        str = " $str"
        str = str.replace(" @", " ")
        mCommentsListViewModel.addComment(
            false,
            notificationData.posts._id,
            str.trim(),
            tagUserList = mTaggedUsersList
        )
    }

    override fun onResume() {
        super.onResume()
    }
}