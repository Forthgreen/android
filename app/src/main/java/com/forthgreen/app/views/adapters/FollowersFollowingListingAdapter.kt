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
import com.forthgreen.app.views.utils.setOnSafeClickListener
import com.forthgreen.app.views.utils.visible
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.row_home_users_rv.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

/**
 * @author Nandita Gandhi
 * @since 15-04-2021
 */
class FollowersFollowingListingAdapter(val loadMoreListener: LoadMoreListener, val clickCallback: UserClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "FollowersFollowingListingAdapter"
        const val ROW_TYPE_USERS = 12
        const val ROW_TYPE_LOAD_MORE = 5
    }

    // Variables
    private var hasMore: Boolean = false
    private val mFollowList = mutableListOf<UserProfile>()
    private var mSelfId = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE ->
                LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else ->
                UsersViewHolder(parent.inflate(R.layout.row_home_users_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UsersViewHolder -> {
                holder.bindUsers(mFollowList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) mFollowList.size + 1
        else mFollowList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            mFollowList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_USERS
        }
    }

    fun submitList(list: List<UserProfile>, selfID: String, hasMore: Boolean, page: Int) {
        if (page == 1) {
            mFollowList.clear()
        }
        mFollowList.addAll(list)
        this.hasMore = hasMore
        if (mSelfId.isEmpty()) {
            this.mSelfId = selfID
        }
        notifyDataSetChanged()
    }

    fun updateUser(user: UserProfile) {
        val index = mFollowList.indexOfFirst { userInfo -> userInfo._id == user._id }
        if (index != -1) {
            // Remove the old user and add the updated one.
            mFollowList.removeAt(index)
            mFollowList.add(index, user)
            notifyItemChanged(index)
        }
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                PushDownAnim.setPushDownAnimTo(btnFollow)

                setOnSafeClickListener {
                    clickCallback.openUserProfile(mFollowList[adapterPosition])
                }

                btnFollow.setOnClickListener {
                    clickCallback.performClickAction(mFollowList[adapterPosition], follow = btnFollow.isChecked)

                    // Invert the status till API hit
                    btnFollow.isChecked = !btnFollow.isChecked
                }
            }
        }

        fun bindUsers(user: UserProfile) {
            // Assign values
            itemView.apply {
                user.apply {
                    civUserImage.loadURL(image, true)
                    if (user._id == mSelfId) {
                        btnFollow.gone()
                    } else {
                        btnFollow.visible()
                        btnFollow.isChecked = isFollow
                    }
                    tvUserFullName.text = firstName
                    if (usernameText.isNotEmpty()) {
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
        fun performClickAction(user: UserProfile, follow: Boolean)
        fun openUserProfile(user: UserProfile)
    }
}