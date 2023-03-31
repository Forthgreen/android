package com.forthgreen.app.views.fragments

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SearchUsersViewModel
import com.forthgreen.app.views.adapters.SearchUsersAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim

import kotlinx.android.synthetic.main.toolbar.*
import android.view.MotionEvent
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.fragment_search_users_invites_friends.*


/**
 * @author Nandita Gandhi
 * @since 12-04-2021
 */
class SearchUsersInviteFriendsFragment : BaseRecyclerViewFragment(), SearchUsersAdapter.UserClickCallback,
    LoadMoreListener {

    companion object {
        const val TAG = "SearchUsersFragment"
      //  private const val mResultSize = 75
        private const val mResultSize = 15
        private val CONTACTS_READ_REQ_CODE = 100

    }

    // Variables
    private var page = 1
    private val mAdapter by lazy { SearchUsersAdapter(this, this) }
    private val mSearchUsersViewModel by lazy {
        // Get View Model
        ViewModelProvider(this).get(SearchUsersViewModel::class.java)
    }
    private val searchText: String
        get() = etSearchUsers.trimmedText

    private var mUser = UserProfile()

    private val selfId: String
        get() = mSearchUsersViewModel.getUserProfileDataFromSharedPrefs()._id

    override val layoutId: Int
        get() = R.layout.fragment_search_users_invites_friends

    override val viewModel: BaseViewModel?
        get() = mSearchUsersViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Push down anim
        PushDownAnim.setPushDownAnimTo(tvCancelSearch)

        // Start shimmer
        flShimmer.startShimmer()
        flShimmer.visible()

        tvToolbarTitle.text = context?.getString(R.string.find_users)
        ivToolbarActionEnd.gone()
        tvCancelSearch.gone()
        toolbar.elevation = 0.0f
        // Search all users firstly when searched text is empty.
        mSearchUsersViewModel.searchUsers(true, page, mResultSize, searchText)
       // mSearchUsersViewModel.searchUsers(true, page, mResultSize, searchText)
    }

    private fun setupListeners() {
        etSearchUsers.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                tvInvitesFriends.gone()
                tvSuggestedUsers.gone()
                viewDividerInvitesFriends.gone()
                etSearchUsers.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_icon), null, ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross_clear), null);
                // Start shimmer
                flShimmer.startShimmer()
                flShimmer.visible()
                recyclerView.gone()
              //  swipeRefreshLayout.gone()
                mSearchUsersViewModel.searchUsers(true, page, mResultSize, text)
               // recyclerView.visible()
            } else {
                tvInvitesFriends.visible()
                tvSuggestedUsers.visible()
                viewDividerInvitesFriends.visible()
                etSearchUsers.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_icon), null,null, null);
              //  mAdapter.submitList(emptyList(), selfId, page, false)
                recyclerView.visible()
            }
        }

        tvCancelSearch.setOnClickListener {
            mAdapter.submitList(emptyList(), selfId, page, false)
            recyclerView.gone()
            flShimmer.startShimmer()
            flShimmer.visible()
            etSearchUsers.setText("")
            hideKeyboard()
            tvCancelSearch.gone()
            tvInvitesFriends.visible()
            tvSuggestedUsers.visible()
            viewDividerInvitesFriends.visible()

            etSearchUsers.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_icon), null,null, null);
            mSearchUsersViewModel.searchUsers(true, page, mResultSize, searchText)
            recyclerView.visible()
        }

        tvInvitesFriends.setOnClickListener {
            if (isAllGranted(Permission.READ_CONTACTS)) {
                performFragTransaction(InvitesUsersFragment(), InvitesUsersFragment.TAG,  enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
            } else {
                askForPermissions(Permission.READ_CONTACTS) { result ->
                    if (result.isAllGranted(Permission.READ_CONTACTS)) {
                        performFragTransaction(InvitesUsersFragment(), InvitesUsersFragment.TAG,  enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                            popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
                    } else {
                        showMessage(R.string.contact_permission_rationale)
                    }
                }
            }
        }

        etSearchUsers.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (etSearchUsers.text.isNotEmpty()) {
                    if (event.rawX >= etSearchUsers.getRight() - etSearchUsers.getCompoundDrawables()
                            .get(DRAWABLE_RIGHT).getBounds().width()
                    ) {
                        if (!etSearchUsers.getText().toString().equals("")) {
                            mAdapter.submitList(emptyList(), selfId, page, false)
                            recyclerView.gone()
                            etSearchUsers.setText("")
                            hideKeyboard()
                            flShimmer.startShimmer()
                            flShimmer.visible()

                            tvInvitesFriends.visible()
                            tvSuggestedUsers.visible()
                            tvCancelSearch.gone()
                            mSearchUsersViewModel.searchUsers(true, page, mResultSize, searchText)
                            recyclerView.visible()
                        } else {
                            etSearchUsers.setCompoundDrawablesWithIntrinsicBounds(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_search_icon
                                ), null, null, null
                            )
                            hideKeyboard()
                            recyclerView.gone()
                        }
                        return@OnTouchListener true
                    }
                } else {
                    recyclerView.gone()
                    tvCancelSearch.visible()
                    tvInvitesFriends.gone()
                    tvSuggestedUsers.gone()
                    viewDividerInvitesFriends.gone()
                }
            }
            false
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_READ_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            performFragTransaction(InvitesUsersFragment(), InvitesUsersFragment.TAG,  enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
        }
    }

    private fun observeProperties() {
        mSearchUsersViewModel.onSuccessfulUsersFetched().observe(viewLifecycleOwner, { fetchedUsers ->
            // Stop shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()

            if (fetchedUsers.data.isEmpty() && page == 1) {
                mAdapter.submitList(emptyList(), selfId, page, hasMore = false)
                showNoDataText(resId = R.string.no_data_to_show)
            } else {
                hideNoDataText()
                mAdapter.submitList(fetchedUsers.data, selfId, page, hasMore = false)
                recyclerView.visible()
            }
        })
        mSearchUsersViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, { statusUpdated ->
            if (statusUpdated) {
                // Update follow status via adapter
                mAdapter.updateUser(mUser)

                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(PostsFeedFragment.LOCAL_INTENT_ACTION_PERFORMED))
            }
        })
    }

    override fun onPullDownToRefresh() {
        page = 1
        if (searchText.isNotEmpty()) {
            // Start shimmer
            flShimmer.startShimmer()
            flShimmer.visible()
          //  swipeRefreshLayout.gone()
            mSearchUsersViewModel.searchUsers(false, page, mResultSize, searchText)
        } else {
            hideSwipeRefreshLoader()
        }
    }

    override fun onLoadMore() {

    }

    override fun performUserClickAction(userData: UserProfile, openProfile: Boolean, follow: Boolean) {
        if (openProfile) {
            // Open profile of user
            performFragTransaction(OtherUserProfileFragment.newInstance(userData), OtherUserProfileFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
        } else {
            // Hit the follow API
            mSearchUsersViewModel.updateUserFollowStatus(isShowLoading = true, status = follow, userRef = userData._id)
            mUser = userData.copy(isFollow = follow)
        }
    }

    override fun onStop() {
        hideKeyboard()
        super.onStop()
    }
}