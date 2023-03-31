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
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.row_home_users_rv.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

/**
 * @author Nandita Gandhi
 * @since 12-04-2021
 */
class SearchUsersAdapter(
        private val clickListener: UserClickCallback,
        private val loadMore: LoadMoreListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "SearchUsersAdapter"
        const val ROW_TYPE_ELEMENT = 2
        const val ROW_TYPE_LOAD_MORE = 3
    }

    // Variables
    private var hasMore = false
    private val mUsersList = mutableListOf<UserProfile>()
    private var mSelfId = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_ELEMENT -> SearchUsersViewHolder(parent.inflate(R.layout.row_home_users_rv))
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> throw IllegalArgumentException("No Such ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchUsersViewHolder -> {
                holder.bindUsers(mUsersList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            mUsersList.size + 1
        } else {
            mUsersList.size
        }
    }

    fun submitList(listOfUsers: List<UserProfile>, selfID: String, page: Int, hasMore: Boolean) {
        if (page == 1) {
            mUsersList.clear()
        }
        if (mSelfId.isEmpty()) {
            this.mSelfId = selfID
        }
        this.hasMore = hasMore
        mUsersList.addAll(listOfUsers)
        notifyDataSetChanged()
    }

    fun updateUser(user: UserProfile) {
        val index = mUsersList.indexOfFirst { userInfo -> userInfo._id == user._id }
        if (index != -1) {
            // Remove the old user and add the updated one.
            mUsersList.removeAt(index)
            mUsersList.add(index, user)
            notifyItemChanged(index)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            mUsersList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    inner class SearchUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                setOnClickListener {
                    clickListener.performUserClickAction(mUsersList[adapterPosition], openProfile = true)
                }
                PushDownAnim.setPushDownAnimTo(btnFollow)
                btnFollow.setOnClickListener {
                    clickListener.performUserClickAction(mUsersList[adapterPosition], follow = btnFollow.isChecked)

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
                    tvUserFullName.text = firstName

                    if (usernameText.isNotEmpty()) {
                        tvUserName.text = usernameText
                        tvUserName.gone()
                       // tvUserName.visible()
                    } else {
                        tvUserName.gone()
                    }

                    if (bio.isNotEmpty()) {
                        tvUserBio.visible()
                        tvUserBio.text = bio
                    } else {
                        tvUserBio.gone()
                    }
                    if (user._id == mSelfId || dummyUser) {
                        btnFollow.gone()
                    } else {
                        btnFollow.visible()
                        btnFollow.isChecked = isFollow
                    }
                }
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                //Assign Values
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMore.onLoadMore()
            }
        }
    }

    interface UserClickCallback {
        fun performUserClickAction(userData: UserProfile, openProfile: Boolean = false, follow: Boolean = false)
    }
}
