package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.CommentsListingViewModel
import com.forthgreen.app.views.adapters.CommentsListAdapter
import com.forthgreen.app.views.adapters.TagUsersAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_comments_list.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Nandita Gandhi
 * @since 16-04-2021
 */
class CommentsListFragment : BaseRecyclerViewFragment(), LoadMoreListener,
        CommentsListAdapter.CommentViewsClickCallbacks, TagUsersAdapter.TagListingCallback {

    companion object {
        const val TAG = "CommentsListFragment"
        private const val BUNDLE_EXTRAS_POST_REF = "BUNDLE_EXTRAS_POST_REF"
        const val LOCAL_INTENT_COMMENTS_ACTION = "LOCAL_INTENT_COMMENTS_ACTION"

        fun newInstance(mPostRef: String): CommentsListFragment {
            return CommentsListFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_POST_REF, mPostRef)
                }
            }
        }
    }

    // Variables
    private var mPage = 1
    private var mTaggedUsersList = mutableListOf<Users>()

    private val mPostRef by lazy { requireArguments().getString(BUNDLE_EXTRAS_POST_REF, "") }

    private val mAdapter by lazy { CommentsListAdapter(this, this) }

    private val tagAdapter by lazy {
        TagUsersAdapter(this, this)
    }

    private val mCommentsListViewModel by lazy {
        // get View Model
        ViewModelProvider(this).get(CommentsListingViewModel::class.java)
    }
    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }
    private var inputMode: Int = 0

    private var comment = Comment()

    private val selfId: String
        get() = mCommentsListViewModel.getUserProfileDataFromSharedPrefs()._id

    override val layoutId: Int
        get() = R.layout.fragment_comments_list

    override val viewModel: BaseViewModel?
        get() = mCommentsListViewModel

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
        tvToolbarTitle.text = getString(R.string.comments_label)

        // Push Down anim
        PushDownAnim.setPushDownAnimTo(tvPostComment)

        // Start Shimmer
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()
        groupAddComment.gone()

        // Fetch comments list
        mCommentsListViewModel.fetchCommentsList(
                mShowLoader = false,
                page = mPage,
                postRef = mPostRef
        )

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(
                mLocalBroadcastReceiver,
                IntentFilter(RepliesFragment.LOCAL_INTENT_REPLY_COMMENTS_ACTION)
        )

        recyclerViewTags.adapter = tagAdapter
        recyclerViewTags.layoutManager = LinearLayoutManager(requireContext())

        // Get original input mode
        inputMode = requireActivity().window.attributes.softInputMode
    }

    private fun setupListeners() {
        etComment.doOnTextChanged { text, start, before, count ->
            if ('@' in text) {
                val pos = text.lastIndexOf('@')
                if (pos == 0 || text.get(pos - 1) == ' ') {
                    val str = text.substring(pos + 1)
                    mCommentsListViewModel.getUsersToTag(false, str)
                }
            } else {
                recyclerViewTags.gone()
                recyclerView.visible()
                swipeRefreshLayout.visible()
            }
        }
        tvPostComment.setOnClickListener {
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
                mPostRef,
                str.trim(),
                tagUserList = mTaggedUsersList
        )
    }

    private fun observeProperties() {
        mCommentsListViewModel.onCommentsFetched().observe(viewLifecycleOwner, { commentsFetched ->
            // Hide Shimmer
//            flShimmer.stopShimmer()
//            flShimmer.gone()
//            swipeRefreshLayout.visible()
            groupAddComment.visible()

            if (commentsFetched.data.isEmpty() && mPage == 1) {
                mAdapter.submitList(emptyList(), false, mPage, selfId)
                showNoDataText(resId = R.string.no_comments_label)
            } else {
                hideNoDataText()
                mAdapter.submitList(commentsFetched.data, commentsFetched.hasMore, mPage, selfId)
            }
        })
        mCommentsListViewModel.onCommentAdded().observe(viewLifecycleOwner, { addedComment ->
            hideNoDataText()
            mAdapter.addComment(addedComment.data)
            // Send Broadcast
            LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(LOCAL_INTENT_COMMENTS_ACTION))
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
                        .sendBroadcast(Intent(LOCAL_INTENT_COMMENTS_ACTION))
            }
        })
        mCommentsListViewModel.onCommentReported().observe(viewLifecycleOwner, { reportSuccess ->
            if (reportSuccess) {
                performFragTransaction(ReportSentFragment.newInstance(), ReportSentFragment.TAG)
            }
        })
        mCommentsListViewModel.onTagsListSuccess().observe(viewLifecycleOwner, { tagList ->
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
        hideNoDataText()
        //Show Shimmer
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()
        groupAddComment.gone()
        mCommentsListViewModel.fetchCommentsList(
                mShowLoader = false,
                page = mPage,
                postRef = mPostRef
        )
    }

    override fun onLoadMore() {
        mPage++
        mCommentsListViewModel.fetchCommentsList(
                mShowLoader = false,
                page = mPage,
                postRef = mPostRef
        )
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                onPullDownToRefresh()
            }
        }
    }

    override fun performCommentClickAction(commentInfo: Comment, clickType: Int, commentLiked: Boolean) {
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
                        RepliesFragment.newInstance(mPostRef, commentInfo), RepliesFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
            }
            ValueMapping.onLikeOrDislike() -> {
                // Vibrate phone
                GeneralFunctions.vibratePhone(requireContext())
                mCommentsListViewModel.updateCommentLike(
                        true,
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

    override fun onTagListingClick(taggedUser: Users) {
        var fullComment: String = etComment.text.toString()
        if ('@' in fullComment) {
            val pos = fullComment.lastIndexOf('@')
            fullComment = fullComment.substring(0, pos + 1)
            fullComment += taggedUser.name + " "
        }
        etComment.setText(fullComment)
        etComment.setSelection(etComment.text.length)
        mTaggedUsersList.add(
                Users(
                        _id = taggedUser._id,
                        name = taggedUser.name,
                        type = taggedUser.type
                )
        )
        recyclerViewTags.gone()
        recyclerView.visible()
        swipeRefreshLayout.visible()

        // Fetch comments list
        mCommentsListViewModel.fetchCommentsList(
                mShowLoader = false,
                page = mPage,
                postRef = mPostRef
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

    override fun onDestroyView() {
        mTaggedUsersList.clear()
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
        requireActivity().window.setSoftInputMode(inputMode)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
}