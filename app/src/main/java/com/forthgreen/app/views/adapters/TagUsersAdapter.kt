package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Users
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_home_users_rv.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

class TagUsersAdapter(
    private val loadMore: LoadMoreListener,
    private val tagListingCallback: TagListingCallback
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "TagUsersAdapter"
        const val ROW_TYPE_ELEMENT = 2
        const val ROW_TYPE_LOAD_MORE = 3
    }

    // Variables
    private var hasMore = false
    private val mUsersList = mutableListOf<Users>()
    private var mSelfId = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_ELEMENT -> TagUsersViewHolder(parent.inflate(R.layout.row_tag_users_rv))
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> throw IllegalArgumentException("No Such ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TagUsersViewHolder -> {
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

    fun submitList(listOfUsers: List<Users>, hasMore: Boolean) {
        mUsersList.clear()
        this.hasMore = hasMore
        mUsersList.addAll(listOfUsers)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            mUsersList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    inner class TagUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                tagListingCallback.onTagListingClick(mUsersList[adapterPosition])
            }
        }

        fun bindUsers(user: Users) {
            // Assign values
            itemView.apply {
                user.apply {
                    civUserImage.loadURL(image, true)
                    tvUserFullName.text = name
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

    interface TagListingCallback {
        fun onTagListingClick(taggedUser: Users)
    }
}