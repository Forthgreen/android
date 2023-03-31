package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Notification
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.makeTextLink
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_notifications_rv.view.*

/**
 * @author Nandita Gandhi
 * @since 09-04-2021
 */
class NotificationsAdapter(val loadMoreListener: LoadMoreListener, private val clickCallback: NotificationsClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "NotificationsAdapter"
        private const val ROW_TYPE_LOAD_MORE = 9
        private const val ROW_TYPE_ELEMENT = 2
    }

    private var hasMore = false
    private val mNotificationList = mutableListOf<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> NotificationsViewHolder(parent.inflate(R.layout.row_notifications_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotificationsViewHolder -> {
                holder.bind(mNotificationList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotificationList.size
    }

    fun submitList(listOfNotifs: List<Notification>, page: Int, hasMore: Boolean) {
        if (page == 1) {
            mNotificationList.clear()
        }
        mNotificationList.addAll(listOfNotifs)
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    fun updateNotification(notification: Notification) {
        val index = mNotificationList.indexOfFirst { notif -> notif._id == notification._id }
        if (index != -1) {
            // Remove the old notification and add the updated notification.
            mNotificationList.removeAt(index)
            mNotificationList.add(index, notification)
            notifyItemChanged(index)
        }
    }

    inner class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickCallback.performNotificationClick(mNotificationList[adapterPosition])
            }
            itemView.tvNotification.setOnClickListener {
                clickCallback.performNotificationClick(mNotificationList[adapterPosition])
            }
        }

        fun bind(notification: Notification) {
            itemView.apply {
                // Assign Values
                notification.apply {
                    tvNotification.text = notificationText
                    civUserImage.loadURL(image, true)
                    tvTimeStamp.text = GeneralFunctions.getRelativeTimeStamp(createdOn)
                    tvNotification.makeTextLink(name, underlined = false, bold = true)
                    if (seen) {
                        itemView.setBackgroundResource(R.color.colorWhite)
                    } else {
                        itemView.setBackgroundResource(R.color.colorNotificationBG)
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

    interface NotificationsClickCallback {
        fun performNotificationClick(notification: Notification)
    }
}