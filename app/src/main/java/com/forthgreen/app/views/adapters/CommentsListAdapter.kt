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

/**
 * @author Nandita Gandhi
 * @since 16-04-2021
 */
class CommentsListAdapter(val loadMoreListener: LoadMoreListener, val commentClickCallback: CommentViewsClickCallbacks) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "CommentsListAdapter"
        const val ROW_TYPE_ELEMENT = 9
        const val ROW_TYPE_LOAD_MORE = 6
    }

    // Variables
    private var hasMore = false
    private val commentList = mutableListOf<Comment>()
    private var selfId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> CommentsViewHolder(parent.inflate(R.layout.row_comments_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentsViewHolder -> {
                holder.bind(commentList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) commentList.size + 1
        else commentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            commentList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    fun submitList(listOfComments: List<Comment>, hasMore: Boolean, page: Int, selfID: String) {
        if (page == 1) {
            commentList.clear()
        }
        commentList.addAll(listOfComments)
        this.hasMore = hasMore
        if (this.selfId.isEmpty()) {
            this.selfId = selfID
        }
        notifyDataSetChanged()
    }

    fun addComment(comment: Comment) {
        commentList.add(itemCount, comment)
        notifyItemInserted(itemCount)
    }

    fun updateComment(comment: Comment) {
        val index = commentList.indexOfFirst { commentInfo -> commentInfo._id == comment._id }
        if (index != -1) {
            // Remove the old comment and add the updated one.
            commentList.removeAt(index)
            commentList.add(index, comment)
            notifyItemChanged(index)
        }
    }

    fun removeComment(comment: Comment) {
        val index = commentList.indexOfFirst { commentInfo -> commentInfo._id == comment._id }
        if (index != -1) {
            // Remove the comment
            commentList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.apply {
                PushDownAnim.setPushDownAnimTo(tvLoadMoreComments, ivMessage, ivCommentOptions, tvReplies, cbLike, tvViewMore)

                tvViewMore.setOnClickListener(this@CommentsViewHolder)
                tvLoadMoreComments.setOnClickListener(this@CommentsViewHolder)
                ivCommentOptions.setOnClickListener(this@CommentsViewHolder)
                cbLike.setOnClickListener(this@CommentsViewHolder)
//                ivMessage.setOnClickListener(this@CommentsViewHolder)
//                tvReplies.setOnClickListener(this@CommentsViewHolder)
                civUserImage.setOnClickListener(this@CommentsViewHolder)
                tvUserFullName.setOnClickListener(this@CommentsViewHolder)
                tvLikes.setOnClickListener(this@CommentsViewHolder)
            }
        }

        fun bind(commentInfo: Comment) {
            // Assign Values
            itemView.apply {
                if (commentList.isNotEmpty() && adapterPosition == commentList.lastIndex && hasMore) {
                    tvLoadMoreComments.visible()
                } else {
                    tvLoadMoreComments.gone()
                }
                commentInfo.apply {
                    if (addedBy.dummyUser && selfId != addedBy._id) {
                        ivCommentOptions.gone()
                    } else {
                        ivCommentOptions.visible()
                    }
                    civUserImage.loadURL(addedBy.image, true)
                    tvUserFullName.text = addedBy.firstName
                    tvComment.text = comment
                    tvComment.maxLines = Constants.MAX_LINES_LIMIT
                    cbLike.isChecked = isLike
                    tvReplies.text = repliesText
                    tvLikes.text = likesText

                    tags.forEach { tag ->
                        if (tvComment.trimmedText.contains(tag.name)) {
                            tvComment.makeTextLink(str = tag.name, underlined = false, bold = false,
                                    color = R.color.colorTaggedUser,
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
                    R.id.tvLoadMoreComments -> {
                        loadMoreListener.onLoadMore()
                        tvLoadMoreComments.text = resources.getString(R.string.loading_label)
                    }
                    R.id.ivCommentOptions -> {
                        commentClickCallback.performCommentClickAction(commentList[adapterPosition], ValueMapping.onMenuClick())
                    }
                    R.id.cbLike -> {
                        commentClickCallback.performCommentClickAction(commentList[adapterPosition], ValueMapping.onLikeOrDislike(), commentLiked = cbLike.isChecked)
                        // Invert the status till API hit
                        cbLike.isChecked = !cbLike.isChecked
                    }
                    R.id.ivMessage -> {
//                        commentClickCallback.performCommentClickAction(commentList[adapterPosition], ValueMapping.onCommentsOrRepliesClick())
                    }
                    R.id.tvReplies -> {
//                        commentClickCallback.performCommentClickAction(commentList[adapterPosition], ValueMapping.onCommentsOrRepliesClick())
                    }
                    R.id.civUserImage, R.id.tvUserFullName -> {
                        commentClickCallback.openUserProfile(commentList[adapterPosition].addedBy)
                    }
                    R.id.tvLikes -> {
                        commentClickCallback.showUsersWhoLiked(commentList[adapterPosition])
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

    interface CommentViewsClickCallbacks {
        fun performCommentClickAction(commentInfo: Comment, clickType: Int, commentLiked: Boolean = false)
        fun openUserProfile(user: UserProfile)
        fun showUsersWhoLiked(commentInfo: Comment)
        fun onTaggedUserClick(taggedUser: Users)
    }
}