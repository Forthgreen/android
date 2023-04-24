package com.forthgreen.app.views.fragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Tab
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.HomeFeedViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.setVisibility
import com.forthgreen.app.views.utils.supportFragmentManager
import kotlinx.android.synthetic.main.fragment_category_filter_tab.toolbar
import kotlinx.android.synthetic.main.fragment_category_filter_tab.tvToolbarTitle
import kotlinx.android.synthetic.main.fragment_posts_feed.*

/**
 * @author Nandita Gandhi
 * @since 05-08-2021
 */
class PostFeedFilterTabFragment : BaseTabLayoutFragment() {

    companion object {
        const val TAG = "PostFeedFilterTabFragment"
        private const val resultSize = 10
    }

    // Variables
    private var page = 1

    override val layoutId: Int
        get() = R.layout.fragment_post_feed_filter_tab

    private val mHomeFeedViewModel by lazy {
        // Get ViewModel
        ViewModelProvider(this).get(HomeFeedViewModel::class.java)
    }

    private lateinit var mLinearLayoutManager: LinearLayoutManager


    override val viewModel: BaseViewModel?
        get() = mHomeFeedViewModel

    override fun initTabs() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {

        // Set up Linear Layout Manager
        mLinearLayoutManager = LinearLayoutManager(requireContext())

        toolbar.navigationIcon = null
        tvToolbarTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_forthgreen_home_feed, 0, 0, 0)

        val listOfTabs = ArrayList<Tab>()

        listOfTabs.add(Tab(PostsFeedFragment(), getString(R.string.new_lable), null, true))
        listOfTabs.add(Tab(PostsFeedFollowingFragment(), getString(R.string.following_label), null, true))

        setViewPagerWithOutToolbarTitle(listOfTabs)

        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            // Fetch notifications
            mHomeFeedViewModel.fetchNotifications(false, page, resultSize)
        }
    }

    private fun setupListeners() {
        ivSearchUsers.setOnClickListener {
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_MUTE))
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
              //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
                performFragTransaction(SearchUsersInviteFriendsFragment(), SearchUsersInviteFriendsFragment.TAG,  enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            }
        }

        ivCreatePost.setOnClickListener {
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_MUTE))
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
              //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
                performFragTransaction(CreatePostFragment(), CreatePostFragment.TAG,
                    enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down)
            }
        }

        ivNotifications.setOnClickListener {
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_MUTE))
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
               // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            } else {
//                mHomeFeedViewModel.saveUserDetailsForNotifDot(mHomeFeedViewModel.getUserProfileDataFromSharedPrefs()
//                    .copy(showNotifyDot = false))
//                ApplicationGlobal.showNotificationDot = false
              //  mHomeFeedViewModel.updateNotifStatus(false)
                ivNotifDot.setVisibility(false)
                performFragTransaction(
                    NotificationsFragment(), NotificationsFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
            }
        }
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }

    private fun observeProperties() {
        mHomeFeedViewModel.onNotificationsFetched().observe(viewLifecycleOwner,
            { fetchedNotifications ->
                if (fetchedNotifications.hasUnreadNotifs && ApplicationGlobal.showNotificationDot) {
                    ivNotifDot.setVisibility(true)
                } else {
                    ivNotifDot.setVisibility(false)
                }
            })
    }

    fun scrollToTop() {
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        mLinearLayoutManager.startSmoothScroll(smoothScroller)
    }

    private fun callUserLoginDialog() {
        val userLoginDialog = UserLoginDialog()
        userLoginDialog.showUserLoginDialog(requireActivity() as AppCompatActivity, object : LoginButtonClickInterface {
            override fun loginButtonClick() {
                performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            }
        })
    }
}