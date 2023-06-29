package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Notification
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.services.MyFirebaseMessagingService
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.NotificationsViewModel
import com.forthgreen.app.views.adapters.NotificationsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * @author Nandita Gandhi
 * @since 09-04-2021
 */
class NotificationsFragment : BaseRecyclerViewFragment(), LoadMoreListener, NotificationsAdapter.NotificationsClickCallback {

    companion object {
        const val TAG = "NotificationsFragment"
        private const val mResultSize = 50
        const val LOCAL_INTENT_NOTIFICATION_SEEN = "LOCAL_INTENT_NOTIFICATION_SEEN"
    }

    // Variables
    private var page = 1

    private var seenNotification = Notification()       // Top level Notification Object

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val mAdapter by lazy {
        NotificationsAdapter(this, this)
    }
    private val mNotificationsViewModel by lazy {
        // Get ViewModel
        ViewModelProvider(this).get(NotificationsViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_notifications

    override val viewModel: BaseViewModel?
        get() = mNotificationsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        tvToolbarTitle.text = getString(R.string.notifications_toolbar_title)

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        swipeRefreshLayout.gone()

        // Fetch Notifications List
        mNotificationsViewModel.fetchNotifications(true, page, mResultSize)

        // Register Broadcast Rec
        mLocalBroadcastManager.registerReceiver(
                mLocalBroadcastReceiver,
                IntentFilter().apply {
                  //  addAction(NotificationDetailsFragment.LOCAL_INTENT_NOTIFICATION_REMOVE_ACTION)
                    addAction(NotificationPostDetailsFragment.LOCAL_INTENT_NOTIFICATION_REMOVE_ACTION)
                    addAction(MyFirebaseMessagingService.LOCAL_INTENT_NEW_PUSH_NOTIFICATION)
                }
        )
    }

    private fun setupListeners() {
    }

    private fun observeProperties() {
        mNotificationsViewModel.onNotificationsFetched().observe(viewLifecycleOwner, { fetchedNotifications ->
            // Stop Shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            swipeRefreshLayout.visible()
            if (fetchedNotifications.data.isEmpty() && page == 1) {
                mAdapter.submitList(emptyList(), page, false)
                showNoDataText(resId = R.string.no_notifications_label)
            } else {
                hideNoDataText()
                mAdapter.submitList(fetchedNotifications.data, page, fetchedNotifications.hasMore)
            }
        })
        mNotificationsViewModel.onNotificationReadSuccess().observe(viewLifecycleOwner, { notificationSeen ->
            if (notificationSeen) {
                mAdapter.updateNotification(seenNotification.copy(seen = true))

                // Send broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                        Intent(LOCAL_INTENT_NOTIFICATION_SEEN)
                )
            }
        })
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                onPullDownToRefresh()
            }
        }
    }

    override fun onPullDownToRefresh() {
        page = 1
        // Start shimmer
//        flShimmer.startShimmer()
//        flShimmer.visible()
//        swipeRefreshLayout.gone()
        mNotificationsViewModel.fetchNotifications(false, page, mResultSize)
    }

    override fun onLoadMore() {
        // Do nothing
    }

    override fun performNotificationClick(notification: Notification) {
        seenNotification = notification
        if (!notification.seen) {
            mNotificationsViewModel.markNotificationAsRead(false, notification._id)
        }
        when (notification.refType) {
            ValueMapping.onNotifPostLiked(), ValueMapping.onNotifComment(), ValueMapping.onNotifReply(),
            ValueMapping.onNotifCommentLiked(), ValueMapping.onNotifReplyLiked(), ValueMapping.onNotifTaggedComment(),
            ValueMapping.onNotifTaggedPost(),
            -> {
                performFragTransaction(NotificationPostDetailsFragment.newInstance(notificationId = notification._id,
                        notificationType = notification.refType), NotificationPostDetailsFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
            ValueMapping.onNotifFollowingType() -> {
                performFragTransaction(OtherUserProfileFragment.newInstance(UserProfile(_id = notification.ref)),
                        OtherUserProfileFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
        }
    }

    // notification user profile click
    override fun performUserProfileClick(notification: Notification) {
        performFragTransaction(OtherUserProfileFragment.newInstance(UserProfile(_id = notification.userid)),
            OtherUserProfileFragment.TAG,
            enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}