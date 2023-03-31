package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_likes_rv.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

/**
 * @author Nandita Gandhi
 * @since 26-07-2021
 */
class LikesAdapter(val loadMoreListener: LoadMoreListener, val clickCallback: UserClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "LikesAdapter"
        private const val ROW_TYPE_LOAD_MORE = 2
        private const val ROW_TYPE_ELEMENT = 1
    }

    // Variables
    private var hasMore = false
    private var selfId: String = ""
    private val usersList = mutableListOf<UserProfile>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> LikesViewHolder(parent.inflate(R.layout.row_likes_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LikesViewHolder -> {
                holder.bind(usersList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) usersList.size + 1
        else usersList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            usersList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    fun submitList(listWeGet: List<UserProfile>, hasMore: Boolean, page: Int, selfID: String) {
        if (page == 1) {
            usersList.clear()
        }
        usersList.addAll(listWeGet)
        this.hasMore = hasMore
        if (this.selfId.isEmpty()) {
            this.selfId = selfID
        }
        notifyDataSetChanged()
    }

    fun updateUserInfo(user: UserProfile) {
        val index = usersList.indexOfFirst { userInfo -> userInfo._id == user._id }
        if (index != -1) {
            // Remove the old data and add the updated one.
            usersList.removeAt(index)
            usersList.add(index, user)
            notifyItemChanged(index)
        }
    }

    inner class LikesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickCallback.openUserProfile(usersList[adapterPosition])
            }
            itemView.apply {
                btnFollow.setOnClickListener {
                    clickCallback.updateFollowStatus(usersList[adapterPosition], follow = btnFollow.isChecked)
                    // Invert the status
                    btnFollow.isChecked = !btnFollow.isChecked
                }
            }
        }

        fun bind(user: UserProfile) {
            // Assign values
            itemView.apply {
                user.apply {
                    civUserImage.loadURL(image, true)
                    tvUserFullName.text = firstName
                    if (user._id == selfId || dummyUser) {
                        btnFollow.gone()
                    } else {
                        btnFollow.visible()
                        btnFollow.isChecked = isFollowing
                    }
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

    interface UserClickCallback {
        fun openUserProfile(user: UserProfile)
        fun updateFollowStatus(user: UserProfile, follow: Boolean)
    }
}