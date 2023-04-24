package com.forthgreen.app.views.adapters

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.HomeFeed
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.repository.models.Users
import com.forthgreen.app.repository.networkrequest.WebConstants
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.google.android.exoplayer2.ui.PlayerView
import com.thekhaeng.pushdownanim.PushDownAnim
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.media.VolumeInfo
import im.ene.toro.widget.Container
import kotlinx.android.synthetic.main.row_home_posts_rv.view.*
import kotlinx.android.synthetic.main.row_home_posts_rv.view.tvUserFullName
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_profile_details_rv.view.*

/**
 * @author Nandita Gandhi
 * @since 12-04-2021
 */
class PostsAdapter(val loadMore: LoadMoreListener, val clickCallback: PostsClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "PostsAdapter"
        const val ROW_TYPE_USER_DETAILS = 5
        const val ROW_TYPE_ELEMENT = 1
        const val ROW_TYPE_LOAD_MORE = 8
    }

    // Variables
    private var hasMore: Boolean = false
    private var selfId = ""
    private val postsList = mutableListOf<HomeFeed>()
    private var userProfileDetails = UserProfile()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_USER_DETAILS -> UserDetailsViewHolder(parent.inflate(R.layout.row_profile_details_rv))
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> PostsListViewHolder(parent.inflate(R.layout.row_home_posts_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserDetailsViewHolder -> {
                holder.bind(userProfileDetails)
            }
            is PostsListViewHolder -> {
                holder.bind(postsList[position - 1])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            postsList.size + 2
        } else {
            postsList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ROW_TYPE_USER_DETAILS
            postsList.size + 1 -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    fun submitDetails(listOfPosts: List<HomeFeed>, userDetails: UserProfile, hasMore: Boolean, page: Int, selfId: String) {
        if (page == 1) {
            postsList.clear()
        }
        postsList.addAll(listOfPosts)
        this.userProfileDetails = userDetails
        if (this.selfId.isEmpty()) {
            this.selfId = selfId
        }
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    fun updateUser(user: UserProfile) {
        this.userProfileDetails = user
        notifyItemChanged(0)
    }

    fun updatePost(postData: HomeFeed) {
        val index = postsList.indexOfFirst { postInfo -> postInfo._id == postData._id }
        if (index != -1) {
            // Remove the old post data and add the updated one.
            postsList.removeAt(index)
            postsList.add(index, postData)
//            notifyItemChanged(index + 1)
        }
    }

    fun removePost(postData: HomeFeed) {
        val index = postsList.indexOfFirst { post -> post._id == postData._id }
        if (index != -1) {
            // Remove the post
            postsList.removeAt(index)
            notifyItemRemoved(index + 1)
        }
    }

    fun muteStatusChanged(post: HomeFeed) {
        val index = postsList.indexOfFirst { posts -> posts._id == post._id }
        if (index - 1 > -1) {
            if (postsList[index - 1].video.isNotEmpty()) {
                notifyItemChanged(index - 1)
            }
        }
        if (index < postsList.size) {
            if (postsList[index].video.isNotEmpty()) {
                notifyItemChanged(index)
            }
        }
        if (index - 2 > -1) {
            if (postsList[index - 2].video.isNotEmpty()) {
                notifyItemChanged(index - 2)
            }
        }
        if (index + 2 < postsList.size) {
            if (postsList[index + 2].video.isNotEmpty()) {
                notifyItemChanged(index + 2)
            }
        }
    }

    fun pauseVideo() {
        notifyDataSetChanged()
    }

    inner class UserDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                // Push Down Anim
                PushDownAnim.setPushDownAnimTo(btnFollow, tvFollowers, tvFollowing)

                btnFollow.setOnClickListener {
                    clickCallback.performDetailsClickAction(userProfileDetails,
                        follow = btnFollow.isChecked)

                    // Invert the status till API hit
                    btnFollow.isChecked = !btnFollow.isChecked
                }
                tvFollowers.setOnClickListener {
                    clickCallback.performDetailsClickAction(userProfileDetails, clickType = ValueMapping.onFollowersClick())
                }
                tvFollowing.setOnClickListener {
                    clickCallback.performDetailsClickAction(userProfileDetails, clickType = ValueMapping.onFollowingClick())
                }
            }
        }

        fun bind(user: UserProfile) {
            itemView.apply {
                user.apply {
                    civUserImage.loadURL(image, true)
                    tvUserFullName.text = firstName
                    if (usernameText.isNotEmpty()) {
                        tvUserName.visible()
                        tvUserName.text = usernameText
                    } else {
                        tvUserName.gone()
                    }
                    tvFollowers.text = followersText
                    tvFollowing.text = followingText
                    if (bio.isNotEmpty()) {
                        tvUserBio.text = bio
                        tvUserBio.visible()
                    } else {
                        tvUserBio.gone()
                    }
                    if (user._id == selfId || dummyUser) {
                        btnFollow.gone()
                    } else if (user.isBlock){
                        btnFollow.gone()
                    }else {
                        btnFollow.isChecked = isFollow
                        btnFollow.visible()
                    }
                    // Make total number of followers and following as bold text.
                    tvFollowers.makeTextLink("$followers", underlined = false, bold = true)
                    tvFollowing.makeTextLink("$followings", underlined = false, bold = true)
                }
            }
        }
    }

    inner class PostsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ToroPlayer, View.OnClickListener {
        init {
            itemView.apply {
                // Push Down Anim
                PushDownAnim.setPushDownAnimTo(ivPostMenuOptions, ivMessage, cbLike)

                tvSeeMore.setOnClickListener {
                    tvPostDesc.maxLines = Int.MAX_VALUE
                    tvSeeMore.gone()
                }

                ivPostMenuOptions.setOnClickListener {
                    clickCallback.performPostClickAction(postsList[adapterPosition - 1], ValueMapping.onMenuClick(), userProfileDetails)
                }
                ivMessage.setOnClickListener {
                    clickCallback.performPostClickAction(postInfo = postsList[adapterPosition - 1], ValueMapping.onCommentsOrRepliesClick())
                }
                tvComments.setOnClickListener {
                    clickCallback.performPostClickAction(postInfo = postsList[adapterPosition - 1],
                        ValueMapping.onCommentsOrRepliesClick())
                }
                cbLike.setOnClickListener {
                    clickCallback.performPostClickAction(postInfo = postsList[adapterPosition - 1],
                        ValueMapping.onLikeOrDislike(),
                        postLiked = cbLike.isChecked)
                    // Invert the status till API hit
//                    cbLike.isChecked = !cbLike.isChecked
                    if (postsList[adapterPosition - 1].isLike) {
                        if (postsList[adapterPosition - 1].likes <= 0) {
                            tvLikes.text = "${postsList[adapterPosition - 1].likes} Likes"
                        } else {
                            tvLikes.text = "${postsList[adapterPosition - 1].likes - 1} Likes"
                        }
                    } else {
                        tvLikes.text = "${postsList[adapterPosition - 1].likes + 1} Likes"
                    }
                }
                tvLikes.setOnClickListener {
                    clickCallback.performPostClickAction(postsList[adapterPosition - 1],
                        clickType = ValueMapping.onLikesClick())
                }
                cbMute.setOnClickListener(this@PostsListViewHolder)
                viewVideoPlayer.setOnClickListener {
                    clickCallback.onMuteUnmuteClicked(postsList[adapterPosition - 1])
                    cbMute.performClick()
                }
            }
        }

        val LAYOUT_RES: Int = R.layout.row_home_posts_rv

        var helper: ExoPlayerViewHelper? = null

        var listener: ToroPlayer.EventListener? = null

        private var mediaUri: Uri? = null

        var playerView: PlayerView? = itemView.itemVideoPlayer

        var checkBoxMute: CheckBox = itemView.cbMute
        var progressBar: ProgressBar = itemView.pbVideo
        var playerThumbnail: ImageView = itemView.itemVideoPlayerThumbnail

        // called from Adapter to setup the media
        fun binding(adapter: RecyclerView.Adapter<*>?, item: Uri?, payloads: List<Any?>?) {
            if (item != null) {
                mediaUri = item
            }
        }

        @NonNull
        override fun getPlayerView(): View {
            return playerView!!
        }

        @NonNull
        override fun getCurrentPlaybackInfo(): PlaybackInfo {
            return if (helper != null) helper!!.latestPlaybackInfo else PlaybackInfo()
        }

        override fun initialize(container: Container, playbackInfo: PlaybackInfo) {
            if (helper == null && mediaUri != null) {
                helper = ExoPlayerViewHelper(this, mediaUri!!, null, ApplicationGlobal.config!!)

                if (ApplicationGlobal.muteVideo) {
                    playbackInfo.volumeInfo = VolumeInfo(true, 0.0f)
                } else {
                    playbackInfo.volumeInfo = VolumeInfo(false, 1.0f)
                }
            }
            if (listener == null) {
                listener = object : ToroPlayer.EventListener {
                    override fun onFirstFrameRendered() {
//                        progressBar.gone()
                        playerThumbnail.gone()
                    }

                    override fun onBuffering() {
                        progressBar.visible()
                        playerThumbnail.visible()
                    }

                    override fun onPlaying() {
                        progressBar.gone()
                        playerThumbnail.gone()
                    }

                    override fun onPaused() {
                        progressBar.gone()
                        playerThumbnail.gone()
                    }

                    override fun onCompleted() {
                    }
                }
            }
            helper?.addPlayerEventListener(listener!!)
            helper?.initialize(container, playbackInfo)
            playerThumbnail.visible()
        }

        override fun release() {
            if (helper != null) {
                helper!!.release()
                helper = null
            }
        }

        override fun play() {
            if (helper != null) helper!!.play()
        }

        override fun pause() {
            if (helper != null) helper!!.pause()
        }

        override fun isPlaying(): Boolean {
            return helper != null && helper!!.isPlaying()
        }

        override fun wantsToPlay(): Boolean {
            return ToroUtil.visibleAreaOffset(this, itemView.parent) >= 0.65
        }

        override fun getPlayerOrder(): Int {
            return adapterPosition
        }

        override fun onClick(v: View) {
            if (ApplicationGlobal.muteVideo) {
                checkBoxMute.isChecked = true
                helper?.volume = 1F
                ApplicationGlobal.muteVideo = false
            } else {
                checkBoxMute.isChecked = false
                helper?.volume = 0F
                ApplicationGlobal.muteVideo = true
            }
        }

        fun bind(post: HomeFeed) {
            // Assign Values
            itemView.apply {
                // Assign adapter to viewPager for multiple images
                val mImageViewPagerAdapter = ViewPagerAdapter()
                viewPager.adapter = mImageViewPagerAdapter
                dotIndicator.setViewPager2(viewPager)
                post.apply {
                    when (type) {
                        ValueMapping.onPostWithImageOnly() -> {
                            groupMultiPictures.visible()
                            tvPostDesc.gone()
                            mImageViewPagerAdapter.submitList(image)
                            groupVideoPlay.gone()
                            pbVideo.gone()
                            itemVideoPlayerThumbnail.gone()
                        }
                        ValueMapping.onPostWithImageNText() -> {
                            groupMultiPictures.visible()
                            tvPostDesc.visible()
                            groupVideoPlay.gone()
                            itemVideoPlayerThumbnail.gone()
                            pbVideo.gone()
                            mImageViewPagerAdapter.submitList(image)
                        }
                        ValueMapping.onPostWithTextOnly() -> {
                            groupMultiPictures.gone()
                            tvPostDesc.visible()
                            groupVideoPlay.gone()
                            itemVideoPlayerThumbnail.gone()
                            pbVideo.gone()
                        }
                        ValueMapping.onPostWithVideoOnly() -> {
                            val params = itemVideoPlayer.layoutParams
                            if (videoHeight == 0 && videoWidth == 0) {
                                (params as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
                            } else {
                                val ratio = (videoWidth.toDouble() / videoHeight.toDouble())
                                val dimen = String.format("%.1f", ratio)
                                (params as ConstraintLayout.LayoutParams).dimensionRatio = dimen
                            }
//                            if (videoHeight > 1800) {
//                                params.height = 1800
//                            } else {
//                                params.height = videoHeight
//                            }
                            itemVideoPlayer.layoutParams = params
                            groupMultiPictures.gone()
                            tvPostDesc.gone()
                            groupVideoPlay.visible()
                            itemVideoPlayerThumbnail.visible()
                            binding(this@PostsAdapter,
                                Uri.parse(WebConstants.fetchVideoURL(video)),
                                null)
                            itemVideoPlayerThumbnail.loadURL(thumbnail, false)
                        }
                        ValueMapping.onPostWithVideoNText() -> {
                            val params = itemVideoPlayer.layoutParams
                            if (videoHeight == 0 && videoWidth == 0) {
                                (params as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
                            } else {
                                val ratio = (videoWidth.toDouble() / videoHeight.toDouble())
                                val dimen = String.format("%.1f", ratio)
                                (params as ConstraintLayout.LayoutParams).dimensionRatio = dimen
                            }
//                            if (videoHeight > 1800) {
//                                params.height = 1800
//                            } else {
//                                params.height = videoHeight
//                            }
                            itemVideoPlayer.layoutParams = params
                            groupVideoPlay.visible()
                            itemVideoPlayerThumbnail.visible()
                            itemVideoPlayerThumbnail.loadURL(thumbnail, false)
                            groupMultiPictures.gone()
                            binding(this@PostsAdapter,
                                Uri.parse(WebConstants.fetchVideoURL(video)),
                                null)
                            tvPostDesc.visible()
                        }
                        else -> {
                            groupMultiPictures.gone()
                            tvPostDesc.gone()
                            groupVideoPlay.gone()
                            itemVideoPlayerThumbnail.gone()
                            pbVideo.gone()
                        }
                    }
                    userProfileDetails.apply {
                        civPostUserImage.loadURL(image, true)
                        tvUserFullName.text = firstName
                        tvTimeStamp.text = GeneralFunctions.getRelativeTimeStamp(post.createdOn)
                        if (dummyUser && selfId != _id) {
                            ivPostMenuOptions.gone()
                        } else {
                            ivPostMenuOptions.visible()
                        }
                    }
                    cbLike.isChecked = isLike
                    tvLikes.text = likesText
                    tvComments.text = commentsText
                    tvPostDesc.text = text
                    tvPostDesc.maxLines = Constants.MAX_LINES_LIMIT
                    cbMute.isChecked = !ApplicationGlobal.muteVideo

                    if (type == ValueMapping.onPostWithImageOnly() || type == ValueMapping.onPostWithImageNText()) {
                        if (image.size <= 1) {
                            dotIndicator.gone()
                        } else {
                            dotIndicator.visible()
                        }
                    }

                    tags.forEach { tag ->
                        if (tvPostDesc.trimmedText.contains(tag.name)) {
                            tvPostDesc.makeTextLink(str = tag.name, bold = false,
                                    underlined = false, color = R.color.colorTaggedUser,
                                    action = { clickCallback.onTaggedUserClick(tag) })
                        }
                    }

                    // Make total likes and comments as bold.
                    tvComments.makeTextLink("$comments", underlined = false, bold = true)
                    tvLikes.makeTextLink("$likes", underlined = false, bold = true)
                }

                cbLike.setOnCheckedChangeListener(null)
                // Add Utility to set ViewMore According to line count.
                tvPostDesc.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        try {
                            tvPostDesc.viewTreeObserver.removeOnPreDrawListener(this)
                            /*
                             * Check if text assigned to the view is of length greater than its maxLines,
                             * then show viewMore view; else hide it.
                             */
                            if (tvPostDesc.lineCount > Constants.MAX_LINES_LIMIT &&
                                    tvPostDesc.maxLines <= Constants.MAX_LINES_LIMIT) {
                                tvSeeMore.visible()
                            } else {
                                tvSeeMore.gone()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }
                })
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMore.onLoadMore()
            }
        }
    }

    interface PostsClickCallback {
        fun performPostClickAction(postInfo: HomeFeed, clickType: Int, user: UserProfile = UserProfile(), postLiked: Boolean = false)
        fun performDetailsClickAction(user: UserProfile, follow: Boolean = false, clickType: Int = 0)
        fun onTaggedUserClick(taggedUser: Users)
        fun onMuteUnmuteClicked(userData: HomeFeed)
    }
}