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
import kotlinx.android.synthetic.main.row_home_users_rv.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*


/**
 * @author Nandita Gandhi
 * @since 08-04-2021
 */
class PostsFeedAdapter(
    val loadMoreListener: LoadMoreListener,
    val clickCallback: PostsClickCallback,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "PostsFeedAdapter"
        const val ROW_TYPE_POST_ELEMENT = 12
        const val ROW_TYPE_USER_ELEMENT = 13
        const val ROW_TYPE_LOAD_MORE = 1
    }

    // Variables
    private val homeFeedList = mutableListOf<HomeFeed>()
    private var hasMore = false
    private var selfId = ""

    var helpers: ExoPlayerViewHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            ROW_TYPE_USER_ELEMENT -> HomeUsersViewHolder(parent.inflate(R.layout.row_home_users_rv))
            else -> HomePostsViewHolder(parent.inflate(R.layout.row_home_posts_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomePostsViewHolder -> {
                holder.bind(homeFeedList[position])
            }
            is HomeUsersViewHolder -> {
                holder.bindUsers(homeFeedList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            homeFeedList.size + 1
        } else {
            homeFeedList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == homeFeedList.size -> {
                ROW_TYPE_LOAD_MORE
            }
            homeFeedList[position].type == ValueMapping.onTypeUser() -> {
                ROW_TYPE_USER_ELEMENT
            }
            else -> {
                ROW_TYPE_POST_ELEMENT
            }
        }
    }

    fun submitList(listWeGet: List<HomeFeed>, hasMore: Boolean, page: Int, selfId: String) {
        if (page == 1) {
            homeFeedList.clear()
        }
        homeFeedList.addAll(listWeGet)
        this.hasMore = hasMore
        if (this.selfId.isEmpty()) {
            this.selfId = selfId
        }
        notifyDataSetChanged()
    }

    fun updateFeedItem(feedData: HomeFeed) {
        val index = homeFeedList.indexOfFirst { feedInfo -> feedInfo._id == feedData._id }
        if (index != -1) {
            // Remove the old feed data and add the updated one.
            homeFeedList.removeAt(index)
            homeFeedList.add(index, feedData)
//            notifyItemChanged(index)
        }
    }

    fun removeFeedItem(feedItem: HomeFeed) {
        val index = homeFeedList.indexOfFirst { feedInfo -> feedInfo._id == feedItem._id }
        if (index != -1) {
            // Remove the post
            homeFeedList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun muteStatusChanged(post: HomeFeed) {
        val index = homeFeedList.indexOfFirst { posts -> posts._id == post._id }
        if (index - 1 > -1) {
            if (homeFeedList[index - 1].video.isNotEmpty()) {
                notifyItemChanged(index - 1)
            }
        }
        if (index + 1 < homeFeedList.size) {
            if (homeFeedList[index + 1].video.isNotEmpty()) {
                notifyItemChanged(index + 1)
            }
        }
        if (index - 2 > -1) {
            if (homeFeedList[index - 2].video.isNotEmpty()) {
                notifyItemChanged(index - 2)
            }
        }
        if (index + 2 < homeFeedList.size) {
            if (homeFeedList[index + 2].video.isNotEmpty()) {
                notifyItemChanged(index + 2)
            }
        }
    }

    fun pauseVideo() {
//        if (helpers != null) {
//            helpers!!.release()
//            helpers = null
//        }
        notifyDataSetChanged()
    }

    inner class HomePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ToroPlayer, View.OnClickListener {
        init {
            itemView.apply {
                // Push Down Anim
                PushDownAnim.setPushDownAnimTo(ivPostMenuOptions, ivMessage, cbLike)

                tvSeeMore.setOnClickListener {
                    tvPostDesc.maxLines = Int.MAX_VALUE
                    tvSeeMore.gone()
                }
                viewProfile.setOnClickListener {
                    clickCallback.performPostClickAction(
                            ValueMapping.onProfileClick(),
                            homeFeedList[adapterPosition]
                    )
                }
                ivPostMenuOptions.setOnClickListener {
                    clickCallback.performPostClickAction(
                            clickType = ValueMapping.onMenuClick(),
                            homeFeedList[adapterPosition]
                    )
                }
                ivMessage.setOnClickListener {
                    clickCallback.performPostClickAction(
                            clickType = ValueMapping.onCommentsOrRepliesClick(),
                            homeFeedList[adapterPosition]
                    )
                }
                tvComments.setOnClickListener {
                    clickCallback.performPostClickAction(
                            clickType = ValueMapping.onCommentsOrRepliesClick(),
                            homeFeedList[adapterPosition]
                    )
                }
                cbLike.setOnClickListener {
                    clickCallback.performPostClickAction(
                        clickType = ValueMapping.onLikeOrDislike(),
                        homeFeedList[adapterPosition],
                        postLiked = cbLike.isChecked
                    )
                    // Invert the status till API hit
//                    cbLike.isChecked = !cbLike.isChecked
                    if (homeFeedList[adapterPosition].isLike) {
                        if (homeFeedList[adapterPosition].likes <= 0) {
                            tvLikes.text = "${homeFeedList[adapterPosition].likes} Likes"
                        } else {
                            tvLikes.text = "${homeFeedList[adapterPosition].likes - 1} Likes"
                        }
                    } else {
                        tvLikes.text = "${homeFeedList[adapterPosition].likes + 1} Likes"
                    }
                }
                tvLikes.setOnClickListener {
                    clickCallback.performPostClickAction(
                        clickType = ValueMapping.onLikesClick(),
                        homeFeedList[adapterPosition]
                    )
                }
                cbMute.setOnClickListener(this@HomePostsViewHolder)
                viewVideoPlayer.setOnClickListener {
                    clickCallback.onMuteUnmuteClicked(homeFeedList[adapterPosition])
                    cbMute.performClick()
                }
//                itemVideoPlayer.setOnClickListener {
//                    if (checkVolume() != null && checkVolume() == true) {
//                        setVolume(false)
//                    } else if(checkVolume() != null && checkVolume() == false) {
//                        setVolume(true)
//                    }
//                    if(cbMute.isChecked) {
//                        // Unmute
//                        cbMute.isChecked = !cbMute.isChecked
//                    } else {
//                        // Mute
//                        cbMute.isChecked = !cbMute.isChecked
//                    }
//                }
//                cbMute.setOnClickListener {
//                    cbMute.isChecked = !cbMute.isChecked
//                }
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

//        fun onMuteClicked() {
//            if (!ImageviewModel.mute.get()) {
//                ImageviewModel.mute.set(false)
//                helper!!.volumeInfo.volume = 1.0f
//            } else {
//                ImageviewModel.mute.set(true)
//                helper!!.volumeInfo.volume = 0.0f
//            }
//        }

//        fun onMuteClicked() {
//            if(itemView.cbMute.isChecked) {
//                helper?.volumeInfo = VolumeInfo(false, 1.0f)
//                itemView.cbMute.isChecked = false
//            } else {
//                helper?.volumeInfo = VolumeInfo(true, 0.0f)
//                itemView.cbMute.isChecked = true
//            }
//        }

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
//            helper?.addPlayerEventListener(object : ToroPlayer.EventListener {
//                override fun onFirstFrameRendered() {}
//                override fun onBuffering() {
//                    progressBar.visible()
//                }
//                override fun onPlaying() {
//                    progressBar.gone()
//                }
//                override fun onPaused() {
//                    progressBar.gone()
//                }
//                override fun onCompleted() {}
//            })
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
            helpers = helper

//            val vi = helper?.volumeInfo
//            vi?.volume = 0f
//            vi?.isMute = true
//            if(helper != null && vi != null) {
//                helper!!.volumeInfo = vi
//            }
        }

//        fun setVolume(mute : Boolean) {
//            if(mute) {
//                helper?.volumeInfo = VolumeInfo(mute, 0.0f)
//            } else {
//                helper?.volumeInfo = VolumeInfo(mute, 1.0f)
//            }
//        }

//        fun checkVolume(): Boolean {
//            return helper?.volumeInfo!!.isMute
//        }

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
//            val info = currentPlaybackInfo
//            val volumeInfo = info.volumeInfo
            if (ApplicationGlobal.muteVideo) {
//                volumeInfo.setMute(false)
//                volumeInfo.setVolume(1.0f)
//                info.setVolumeInfo(volumeInfo)
                checkBoxMute.isChecked = true
                helper?.volume = 1F
                ApplicationGlobal.muteVideo = false
            } else {
//                volumeInfo.setMute(true)
//                volumeInfo.setVolume(0.0f)
//                info.setVolumeInfo(volumeInfo)
                checkBoxMute.isChecked = false
                helper?.volume = 0F
                ApplicationGlobal.muteVideo = true
            }
//            helper?.setVolumeInfo(volumeInfo)
//            helper?.setPlaybackInfo(info)
        }

        fun bind(postData: HomeFeed) {
            // Assign Values
            itemView.apply {
                val mImageViewPagerAdapter = ViewPagerAdapter()
                viewPager.adapter = mImageViewPagerAdapter
                dotIndicator.setViewPager2(viewPager)
                postData.apply {
                    if (addedBy.dummyUser && selfId != addedBy._id) {
                        ivPostMenuOptions.gone()
                    } else {
                        ivPostMenuOptions.visible()
                    }
                    when (type) {
                        ValueMapping.onPostWithImageOnly() -> {
                            groupMultiPictures.visible()
                            tvPostDesc.gone()
                            groupVideoPlay.gone()
                            itemVideoPlayerThumbnail.gone()
                            pbVideo.gone()
                            mImageViewPagerAdapter.submitList(image)
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
                            pbVideo.gone()
                            itemVideoPlayerThumbnail.gone()
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
//                            val adapter = PostsFeedAdapter(mFragment, mFragment, mFragment)
                            binding(this@PostsFeedAdapter,
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
                            binding(this@PostsFeedAdapter,
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
                    civPostUserImage.loadURL(addedBy.image, true)
                    tvUserFullName.text = addedBy.firstName
                    tvTimeStamp.text = GeneralFunctions.getRelativeTimeStamp(createdOn)
                    tvLikes.text = likesText
                    tvComments.text = commentsText
                    tvPostDesc.text = text
                    tvPostDesc.maxLines = Constants.MAX_LINES_LIMIT
                    cbLike.isChecked = isLike
                    cbMute.isChecked = !ApplicationGlobal.muteVideo

                    // Show who liked the posts excluding self posts
                    if (whoLiked.isNotEmpty() && postData.addedBy._id != selfId) {
                        val usersLiked =
                                whoLiked.filter { userProfile -> userProfile._id != selfId }
                        when {
                            usersLiked.size > 2 -> {
                               // groupUserLiked.visible()
                                groupUserLiked.gone()
                                val firstUser =
                                        "${usersLiked.first().firstName} ${usersLiked.first().lastName}"
                                val secondUser =
                                        "${usersLiked[1].firstName} ${usersLiked[1].lastName}"
                                tvUserLiked.text = "$firstUser and $secondUser likes this"

                                tvUserLiked.makeTextLink(
                                        str = firstUser,
                                        underlined = false, bold = true
                                )
                                tvUserLiked.makeTextLink(str = secondUser,
                                        underlined = false, bold = true)
                            }
                            usersLiked.size == 1 -> {
                               // groupUserLiked.visible()
                                groupUserLiked.gone()
                                val firstUser = "${usersLiked.first().firstName} ${usersLiked.first().lastName}"
                                tvUserLiked.text = "$firstUser likes this"

                                tvUserLiked.makeTextLink(str = firstUser,
                                        underlined = false, bold = true)
                            }
                            usersLiked.isEmpty() -> {
                                groupUserLiked.gone()
                            }
                        }
                    } else {
                        groupUserLiked.gone()
                    }
                    if (type == ValueMapping.onPostWithImageOnly() || type == ValueMapping.onPostWithImageNText()) {
                        if (image.size <= 1) {
                            dotIndicator.gone()
                        } else {
                            dotIndicator.visible()
                        }
                    }

                    tags.forEach { tag ->
                        if (tvPostDesc.trimmedText.contains(tag.name)) {
                            tvPostDesc.makeTextLink(str = tag.name, underlined = false,
                                    bold = false, color = R.color.colorTaggedUser,
                                    action = { clickCallback.onTaggedUserClick(tag) })
                        }
                    }

                    // Make total likes and comments as bold.
                    tvComments.makeTextLink("$comments", underlined = false, bold = true)
                    tvLikes.makeTextLink("$likes", underlined = false, bold = true)

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
                                        tvPostDesc.maxLines <= Constants.MAX_LINES_LIMIT
                                ) {
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
    }

    inner class HomeUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickCallback.performUserClickAction(homeFeedList[adapterPosition], openProfile = true)
            }
            itemView.apply {
                PushDownAnim.setPushDownAnimTo(btnFollow)

                btnFollow.setOnClickListener {
                    clickCallback.performUserClickAction(homeFeedList[adapterPosition], follow = btnFollow.isChecked)
                    // Invert the status
                    btnFollow.isChecked = !btnFollow.isChecked
                }
            }
        }

        fun bindUsers(userData: HomeFeed) {
            // Assign values
            itemView.apply {
                userData.apply {
                    civUserImage.loadURL(image.firstOrEmpty(), true)
                    tvUserFullName.text = name
                    tvUserName.gone()

                    if (type == ValueMapping.onPostWithImageOnly() || type == ValueMapping.onPostWithImageNText()) {
                        if (image.size <= 1) {
                            dotIndicator.gone()
                        } else {
                            dotIndicator.visible()
                        }
                    }
                    // Uncomment to set and show Username and bio
                    /*if (usernameText.isNotEmpty()) {
                        tvUserName.text = usernameText
                        tvUserName.visible()
                    } else {
                        tvUserName.gone()
                    }
                    if (bio.isNotEmpty()) {
                        tvUserBio.visible()
                        tvUserBio.text = bio
                    } else {
                        tvUserBio.gone()
                    }*/
                    btnFollow.isChecked = isFollowed
                }
            }
        }
    }


    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                // Assign Values
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMoreListener.onLoadMore()
            }
        }
    }

    interface PostsClickCallback {
        fun performPostClickAction(clickType: Int, postData: HomeFeed, postLiked: Boolean = false)
        fun performUserClickAction(userData: HomeFeed, openProfile: Boolean = false, follow: Boolean = false)
        fun onTaggedUserClick(taggedUser: Users)
        fun onMuteUnmuteClicked(userData: HomeFeed)
    }
}