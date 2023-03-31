package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Comment
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.repository.models.Users
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.row_comments_rv.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_replies_rv.view.*
import kotlinx.android.synthetic.main.row_replies_rv.view.cbLike
import kotlinx.android.synthetic.main.row_replies_rv.view.civUserImage
import kotlinx.android.synthetic.main.row_replies_rv.view.tvLikes
import kotlinx.android.synthetic.main.row_replies_rv.view.tvUserFullName
import kotlinx.android.synthetic.main.row_replies_rv.view.tvViewMore

/**
 * @author Nandita Gandhi
 * @since 17-04-2021
 */
class RepliesAdapter(val loadMoreListener: LoadMoreListener, val commentClickCallback: CommentClickCallbacks) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "RepliesAdapter"
        const val ROW_TYPE_REPLY_ELEMENT = 9
        const val ROW_TYPE_COMMENT_ELEMENT = 7
        const val ROW_TYPE_LOAD_MORE = 6
    }

    // Variables
    private var hasMore = false
    private var comment = Comment()
    private val repliesList = mutableListOf<Comment>()
    private var selfId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            ROW_TYPE_COMMENT_ELEMENT -> CommentViewHolder(parent.inflate(R.layout.row_comments_rv))
            else -> RepliesViewHolder(parent.inflate(R.layout.row_replies_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> {
                holder.bind(comment)
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
            is RepliesViewHolder -> {
                holder.bind(repliesList[position - 1])
            }
        }
    }

    override fun getItemCount(): Int {
        return repliesList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ROW_TYPE_COMMENT_ELEMENT
            else -> ROW_TYPE_REPLY_ELEMENT
        }
    }

    fun submitList(listOfReplies: List<Comment>, comment: Comment, hasMore: Boolean, page: Int, selfID: String) {
        if (page == 1) {
            repliesList.clear()
        }
        repliesList.addAll(listOfReplies)
        this.comment = comment
        this.hasMore = hasMore
        if (this.selfId.isEmpty()) {
            this.selfId = selfID
        }
        notifyDataSetChanged()
    }

    // Method to add reply
    fun addReply(reply: Comment) {
        repliesList.add(0, reply)
        notifyItemInserted(1)
        this.comment = this.comment.copy(reply = this.comment.reply + 1)
        notifyDataSetChanged()
    }

    /* Method to update the top comment when index = 0 on click actions;
    * or update replies when index not specified.
    */
    fun updateCommentData(index: Int = -1, comment: Comment) {
        if (index == 0) {
            this.comment = comment
            notifyItemChanged(index)
        } else {
            val indexOfList = repliesList.indexOfFirst { reply -> reply._id == comment._id }
            if (indexOfList != -1) {
                // Remove the old comment and add the updated one.
                repliesList.removeAt(indexOfList)
                repliesList.add(indexOfList, comment)
                notifyItemChanged(indexOfList + 1)
            }
        }
    }

    // Method to remove specific comment
    fun removeComment(comment: Comment) {
        val index = repliesList.indexOfFirst { commentInfo -> commentInfo._id == comment._id }
        if (index != -1) {
            // Remove the comment
            repliesList.removeAt(index)
            notifyItemRemoved(index + 1)
            this.comment = this.comment.copy(reply = this.comment.reply - 1)
            notifyDataSetChanged()
        }
    }

    inner class RepliesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.apply {
                // Push down anim
                PushDownAnim.setPushDownAnimTo(tvViewMore, tvLoadMoreReplies, cbLike, ivReplyOptions)

                tvViewMore.setOnClickListener(this@RepliesViewHolder)
                tvLoadMoreReplies.setOnClickListener(this@RepliesViewHolder)
                cbLike.setOnClickListener(this@RepliesViewHolder)
                ivReplyOptions.setOnClickListener(this@RepliesViewHolder)
                civUserImage.setOnClickListener(this@RepliesViewHolder)
                tvUserFullName.setOnClickListener(this@RepliesViewHolder)
                tvLikes.setOnClickListener(this@RepliesViewHolder)
            }
        }

        fun bind(reply: Comment) {
            itemView.apply {
                if (repliesList.isNotEmpty() && adapterPosition == repliesList.size && hasMore) {
                    tvLoadMoreReplies.visible()
                } else {
                    tvLoadMoreReplies.gone()
                }
                // Assign values
                reply.apply {
                    if (reply.addedBy.dummyUser && selfId != reply.addedBy._id) {
                        ivReplyOptions.gone()
                    } else {
                        ivReplyOptions.visible()
                    }
                    civUserImage.loadURL(addedBy.image, true)
                    tvUserFullName.text = addedBy.firstName
                    tvReply.text = comment
                    tvReply.maxLines = Constants.MAX_LINES_LIMIT
                    tvLikes.text = likesText
                    cbLike.isChecked = isLike

                    tags.forEach { tag ->
                        if (tvReply.trimmedText.contains(tag.name)) {
                            tvReply.makeTextLink(
                                tag.name,
                                false,
                                false,
                                R.color.colorTaggedUser,
                                action = { commentClickCallback.onTaggedUserClick(tag) })
                        }
                    }

                    // Make number of likes as bold
                    tvLikes.makeTextLink("$likes", underlined = false, bold = true)
                }

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
        }

        override fun onClick(v: View?) {
            itemView.apply {
                when (v?.id) {
                    R.id.tvViewMore -> {
                        tvReply.maxLines = Int.MAX_VALUE
                        tvViewMore.gone()
                    }
                    R.id.tvLoadMoreReplies -> {
                        loadMoreListener.onLoadMore()
                        tvLoadMoreReplies.text = resources.getString(R.string.loading_label)
                    }
                    R.id.cbLike -> {
                        commentClickCallback.performClickAction(repliesList[adapterPosition - 1], ValueMapping.onLikeOrDislike(), commentLiked = cbLike.isChecked)
                        // Invert the status till API hit.
                        cbLike.isChecked = !cbLike.isChecked
                    }
                    R.id.ivReplyOptions -> {
                        commentClickCallback.performClickAction(repliesList[adapterPosition - 1], ValueMapping.onMenuClick())
                    }
                    R.id.civUserImage, R.id.tvUserFullName -> {
                        commentClickCallback.openUserProfile(repliesList[adapterPosition - 1].addedBy)
                    }
                    R.id.tvLikes -> {
                        commentClickCallback.showUserWhoLiked(repliesList[adapterPosition - 1])
                    }
                }
            }
        }
    }


    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.apply {
                tvLoadMoreComments.gone()

                PushDownAnim.setPushDownAnimTo(tvLoadMoreComments, ivMessage, ivCommentOptions, tvReplies, cbLike, tvViewMore)

                tvViewMore.setOnClickListener(this@CommentViewHolder)
                ivCommentOptions.setOnClickListener(this@CommentViewHolder)
                cbLike.setOnClickListener(this@CommentViewHolder)
                civUserImage.setOnClickListener(this@CommentViewHolder)
                tvUserFullName.setOnClickListener(this@CommentViewHolder)
                tvLikes.setOnClickListener(this@CommentViewHolder)
            }
        }

        fun bind(comment: Comment) {
            // Assign Values
            itemView.apply {
                comment.apply {
                    if (comment.addedBy.dummyUser && selfId != comment.addedBy._id) {
                        ivCommentOptions.gone()
                    } else {
                        ivCommentOptions.visible()
                    }
                    civUserImage.loadURL(addedBy.image, true)
                    tvUserFullName.text = addedBy.firstName
                    tvComment.text = comment.comment
                    tvComment.maxLines = Constants.MAX_LINES_LIMIT
                    cbLike.isChecked = isLike

                    tvReplies.text = repliesText
                    tvLikes.text = likesText

                    tags.forEach { tag ->
                        if (tvComment.trimmedText.contains(tag.name)) {
                            tvComment.makeTextLink(
                                tag.name,
                                false,
                                false,
                                R.color.colorTaggedUser,
                                action = { commentClickCallback.onTaggedUserClick(tag) })
                        }
                    }

                    // Make number of replies and likes as bold
                    tvReplies.makeTextLink("$reply", underlined = false, bold = true)
                    tvLikes.makeTextLink("$likes", underlined = false, bold = true)
                }
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
        }

        override fun onClick(v: View?) {
            itemView.apply {
                when (v?.id) {
                    R.id.tvViewMore -> {
                        tvComment.maxLines = Int.MAX_VALUE
                        tvViewMore.gone()
                    }
                    R.id.ivCommentOptions -> {
                        commentClickCallback.performClickAction(comment, ValueMapping.onMenuClick(), replyCommentClick = false)
                    }
                    R.id.cbLike -> {
                        commentClickCallback.performClickAction(comment, ValueMapping.onLikeOrDislike(), commentLiked = cbLike.isChecked, replyCommentClick = false)
                        // Invert the status till API hit
                        cbLike.isChecked = !cbLike.isChecked
                    }
                    R.id.civUserImage, R.id.tvUserFullName -> {
                        commentClickCallback.openUserProfile(comment.addedBy)
                    }
                    R.id.tvLikes -> {
                        commentClickCallback.showUserWhoLiked(comment)
                    }
                }
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMoreListener.onLoadMore()
            }
        }
    }

    interface CommentClickCallbacks {
        fun performClickAction(
                commentInfo: Comment, clickType: Int, commentLiked: Boolean = false,
                replyCommentClick: Boolean = true,
        )

        fun openUserProfile(user: UserProfile)
        fun showUserWhoLiked(commentInfo: Comment)
        fun onTaggedUserClick(taggedUser: Users)
    }
}