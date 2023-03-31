package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Tab
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.FollowListViewModel
import kotlinx.android.synthetic.main.fragment_followers_following_tab.*

/**
 * @author Nandita Gandhi
 * @since 15-04-2021
 */
class FollowersFollowingTabFragment : BaseTabLayoutFragment() {

    companion object {
        const val TAG = "FollowersFollowingTabFragment"
        private const val BUNDLE_EXTRAS_CLICK_TYPE = "BUNDLE_EXTRAS_CLICK_TYPE"
        private const val BUNDLE_EXTRAS_USER_INFO = "BUNDLE_EXTRAS_USER_INFO"

        fun newInstance(otherUser: UserProfile = UserProfile(), followersClick: Boolean): FollowersFollowingTabFragment {
            return FollowersFollowingTabFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(BUNDLE_EXTRAS_CLICK_TYPE, followersClick)
                    putParcelable(BUNDLE_EXTRAS_USER_INFO, otherUser)
                }
            }
        }
    }

    // Variables
    private val followersClick by lazy {
        requireArguments().getBoolean(BUNDLE_EXTRAS_CLICK_TYPE, false)
    }
    private val userProfile by lazy {
        requireArguments().getParcelable<UserProfile>(BUNDLE_EXTRAS_USER_INFO)!!
    }
    private val mViewModel by lazy {
        // Get ViewModel
        ViewModelProvider(this).get(FollowListViewModel::class.java)
    }
    private val selfInfo by lazy {
        mViewModel.getUserProfileDataFromSharedPrefs()
    }

    override val layoutId: Int
        get() = R.layout.fragment_followers_following_tab

    override val viewModel: BaseViewModel?
        get() = mViewModel

    override fun initTabs() {
        setupViews()
    }

    private fun setupViews() {
        val listOfTabs = ArrayList<Tab>()

        listOfTabs.add(Tab(FollowersFollowingListingFragment.newInstance(false, userProfile._id), getString(R.string.followers_label), null, true))
        listOfTabs.add(Tab(FollowersFollowingListingFragment.newInstance(true, userProfile._id), getString(R.string.following_label), null, true))

        if (userProfile.firstName.isNotEmpty()) {
            setViewPager(userProfile.firstName, listOfTabs)
        } else {
            setViewPager(selfInfo.firstName, listOfTabs)
        }

        // Set the current index of view pager according to click
        if (followersClick)
            viewPager.currentItem = 0
        else
            viewPager.currentItem = 1
    }
}