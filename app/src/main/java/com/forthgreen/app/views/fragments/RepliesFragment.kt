package com.forthgreen.app.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Comment
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.CommentsListingViewModel
import com.forthgreen.app.views.adapters.RepliesAdapter
import com.forthgreen.app.views.adapters.TagUsersAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_replies.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Nandita Gandhi
 * @since 17-04-2021
 */
class RepliesFragment : BaseRecyclerViewFragment(), LoadMoreListener,
    RepliesAdapter.CommentClickCallbacks,
    TagUsersAdapter.TagListingCallback {

    companion object {
        const val TAG = "RepliesFragment"
        private const val BUNDLE_EXTRAS_POST_REF = "BUNDLE_EXTRAS_POST_REF"
        private const val BUNDLE_EXTRAS_COMMENT_INFO = "BUNDLE_EXTRAS_COMMENT_INFO"
        const val LOCAL_INTENT_REPLY_COMMENTS_ACTION = "LOCAL_INTENT_REPLY_COMMENTS_ACTION"

        fun newInstance(mPostRef: String, mComment: Comment): RepliesFragment {
            return RepliesFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_POST_REF, mPostRef)
                    putParcelable(BUNDLE_EXTRAS_COMMENT_INFO, mComment)
                }
            }
        }
    }

    // Variables
    private var mPage = 1
    private var mTaggedUsersList = mutableListOf<Users>()

    private val mPostRef by lazy {
        requireArguments().getString(BUNDLE_EXTRAS_POST_REF, "")
    }

    private val mComment by lazy {
        requireArguments().getParcelable<Comment>(BUNDLE_EXTRAS_COMMENT_INFO)!!
    }

    private val mAdapter by lazy {
        RepliesAdapter(this, this)
    }

    private val tagAdapter by lazy {
        TagUsersAdapter(this, this)
    }

    private val mRepliesListingViewModel by lazy {
        // Get view Model
        ViewModelProvider(this).get(CommentsListingViewModel::class.java)
    }
    private val selfId: String
        get() = mRepliesListingViewModel.getUserProfileDataFromSharedPrefs()._id

    // To update the top comment on screen on specific changes
    private var comment = Comment()

    // To update the reply of a comment on specific actions
    private var repliedComment = Comment()

    // Boolean to check if action performed is on top comment or on replied one.
    private var repliedCommentAction = false

    override val layoutId: Int
        get() = R.layout.fragment_replies

    override val viewModel: BaseViewModel?
        get() = mRepliesListingViewModel

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
        // Setup toolbar title
        tvToolbarTitle.text = getString(R.string.replies_toolbar_title)

        // Push down anim
        PushDownAnim.setPushDownAnimTo(tvPostReply)

        comment = mComment

        // Send Comment info prior to replies
        mAdapter.submitList(emptyList(), comment, false, mPage, selfId)

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()
        groupAddReply.gone()

        // Fetch replies of specific comment
        mRepliesListingViewModel.fetchCommentsList(
            mShowLoader = false, page = mPage,
            postRef = mPostRef, commentRef = comment._id
        )

        recyclerViewTags.adapter = tagAdapter
        recyclerViewTags.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        etReply.doOnTextChanged { text, start, before, count ->
            if ('@' in text) {
                val pos = text.lastIndexOf('@')
                if (pos == 0 || text.get(pos - 1) == ' ') {
                    val str = text.substring(pos + 1)
                    mRepliesListingViewModel.getUsersToTag(false, str)
                }
            } else {
                recyclerViewTags.gone()
                recyclerView.visible()
                swipeRefreshLayout.visible()
            }
        }
        tvPostReply.setOnClickListener {
            // Add Reply comment
            if (etReply.text.isEmpty()) {
                // Uncomment to show error message.
                //showMessage(resId = R.string.empty_reply_message)
            } else {
                checkForTaggedUsersAndHitAPI()
                etReply.setText("")
                hideKeyboard()
            }
        }
    }

    private fun checkForTaggedUsersAndHitAPI() {
        val usersToRemove = mutableListOf<Users>()
        mTaggedUsersList.forEach { user ->
            if (!(etReply.trimmedText.contains(user.name))) {
                usersToRemove.add(user)
            }
        }
        mTaggedUsersList.removeAll(usersToRemove)
        var str = etReply.text.toString()
        str = " $str"
        str = str.replace(" @", " ")
        mRepliesListingViewModel.addComment(
            false,
            mPostRef,
            str.trim(),
            comment._id,
            tagUserList = mTaggedUsersList
        )
    }

    private fun observeProperties() {
        mRepliesListingViewModel.onCommentsFetched().observe(viewLifecycleOwner, { repliesList ->
            // Hide shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()
            groupAddReply.visible()

            if (repliesList.data.isEmpty() && mPage == 1) {
                mAdapter.submitList(emptyList(), comment = comment, false, mPage, selfId)
                tvNoData.apply {
                    visible()
                    text = getString(R.string.no_replies_label)
                }
            } else {
                tvNoData.gone()
                mAdapter.submitList(repliesList.data, comment, repliesList.hasMore, mPage, selfId)
            }
        })
        mRepliesListingViewModel.onCommentAdded().observe(viewLifecycleOwner, { addedReply ->
            tvNoData.gone()
            // Add reply via adapter
            mAdapter.addReply(addedReply.data)
            // Maintain the top comment data
            comment = comment.copy(reply = comment.reply + 1)
            // Send Broadcast
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_REPLY_COMMENTS_ACTION))
        })
        mRepliesListingViewModel.onCommentLikeUpdated().observe(viewLifecycleOwner, { likeUpdated ->
            if (likeUpdated) {
                // Check if like action is performed on reply or top comment and update accordingly.
                if (repliedCommentAction) {
                    mAdapter.updateCommentData(comment = repliedComment)
                } else {
                    mAdapter.updateCommentData(0, comment)
                }
                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_REPLY_COMMENTS_ACTION))
            }
        })
        mRepliesListingViewModel.onCommentDeleted().observe(viewLifecycleOwner, { deleteSuccess ->
            if (deleteSuccess) {
                // Check if delete action is performed on reply or top comment and update accordingly.
                if (repliedCommentAction) {
                    mAdapter.removeComment(repliedComment)
                    comment = comment.copy(reply = comment.reply - 1)
                } else {
                    showMessage(resId = R.string.remove_success)
                    supportFragmentManager().popBackStack()
                }
                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(LOCAL_INTENT_REPLY_COMMENTS_ACTION))
            }
        })
        mRepliesListingViewModel.onCommentReported().observe(viewLifecycleOwner, { reportSuccess ->
            if (reportSuccess) {
                performFragTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })
        mRepliesListingViewModel.onTagsListSuccess().observe(viewLifecycleOwner, { tagList ->
            if (tagList.data.isEmpty()) {
                recyclerViewTags.gone()
                tagAdapter.submitList(emptyList(), false)
            } else {
                recyclerViewTags.visible()
                recyclerView.gone()
                swipeRefreshLayout.gone()
                hideNoDataText()
                tagAdapter.submitList(tagList.data, false)
            }
        })
    }

    override fun onPullDownToRefresh() {
        mPage = 1
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()
        groupAddReply.gone()
        mRepliesListingViewModel.fetchCommentsList(mShowLoader = false, page = mPage,
                postRef = mPostRef, commentRef = comment._id)
    }

    override fun onLoadMore() {
        mPage++
        mRepliesListingViewModel.fetchCommentsList(mShowLoader = false, page = mPage,
                postRef = mPostRef, commentRef = comment._id)
    }

    override fun performClickAction(commentInfo: Comment, clickType: Int, commentLiked: Boolean, replyCommentClick: Boolean) {
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
                            text = if (replyCommentClick) {
                                getString(R.string.remove_reply_label)
                            } else {
                                getString(R.string.remove_comment_label)
                            }
                            repliedCommentAction = replyCommentClick
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                mRepliesListingViewModel.deleteComment(true, commentInfo._id)
                                // Maintain the comment info according to click type.
                                if (replyCommentClick) {
                                    repliedComment = commentInfo
                                } else {
                                    comment = commentInfo
                                }
                            }
                        }
                    } else {
                        tvReportAbuse.apply {
                            text = if (replyCommentClick) {
                                getString(R.string.report_reply_label)
                            } else {
                                getString(R.string.report_comment_label)
                            }
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText))
                            setOnClickListener {
                                this@show.dismiss()
                                // Hit Report API
                                mRepliesListingViewModel.reportComment(true, commentInfo._id)
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
                mRepliesListingViewModel.updateCommentLike(
                    true,
                    commentInfo._id,
                    liked = commentLiked
                )
                repliedCommentAction = replyCommentClick
                // Update the count of likes according to click performed.
                if (replyCommentClick) {
                    repliedComment = if (commentLiked) {
                        commentInfo.copy(isLike = commentLiked, likes = commentInfo.likes + 1)
                    } else {
                        commentInfo.copy(isLike = commentLiked, likes = commentInfo.likes - 1)
                    }
                } else {
                    comment = if (commentLiked) {
                        commentInfo.copy(isLike = commentLiked, likes = commentInfo.likes + 1)
                    } else {
                        commentInfo.copy(isLike = commentLiked, likes = commentInfo.likes - 1)
                    }
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

    override fun onTagListingClick(taggedUser: Users) {
        var fullComment: String = etReply.text.toString()
        if ('@' in fullComment) {
            val pos = fullComment.lastIndexOf('@')
            fullComment = fullComment.substring(0, pos + 1)
            fullComment += taggedUser.name + " "
        }
        etReply.setText(fullComment)
        etReply.setSelection(etReply.text.length)
        mTaggedUsersList.add(
            Users(
                _id = taggedUser._id,
                name = taggedUser.name,
                type = taggedUser.type
            )
        )
        recyclerViewTags.gone()
        swipeRefreshLayout.visible()
        recyclerView.visible()

        // Fetch replies list
        mRepliesListingViewModel.fetchCommentsList(
            mShowLoader = false, page = mPage,
            postRef = mPostRef, commentRef = comment._id
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

    override fun showUserWhoLiked(commentInfo: Comment) {
        performFragTransaction(
            LikesFragment.newInstance(commentInfo._id), LikesFragment.TAG,
            enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
    }

    override fun onDestroyView() {
        mTaggedUsersList.clear()
        super.onDestroyView()
    }
}