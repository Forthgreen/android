package com.forthgreen.app.views.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ContactsViewModel
import com.forthgreen.app.viewmodels.SearchUsersViewModel
import com.forthgreen.app.views.adapters.InvitesUsersAdapter
import com.forthgreen.app.views.adapters.SearchUsersAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_search_users.*
import kotlinx.android.synthetic.main.toolbar.*
import android.text.TextUtils
import com.forthgreen.app.repository.models.Contact
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import android.os.Handler
import android.database.ContentObserver
import android.database.Cursor
import android.widget.Toast
import android.R.attr.phoneNumber
import android.app.PendingIntent

import com.forthgreen.app.views.utils.SmsSendObserver
import android.app.Activity
import android.content.*
import android.telephony.SmsManager
import android.content.Intent
import android.R.attr.phoneNumber
import android.app.Activity.RESULT_OK
import android.util.Log
import android.provider.Telephony
import android.app.Instrumentation.ActivityResult
import kotlinx.android.synthetic.main.fragment_invites_users.*
import kotlinx.android.synthetic.main.fragment_search_users.flShimmer
import kotlinx.android.synthetic.main.fragment_search_users.recyclerView


/**
 * @author Nandita Gandhi
 * @since 12-04-2021
 */
class InvitesUsersFragment : BaseRecyclerViewFragment(),
    InvitesUsersAdapter.InvitesUserClickCallback {

    companion object {
        const val TAG = "InvitesUsersFragment"
    }

    private val mAdapter by lazy { InvitesUsersAdapter(this) }

    private val mContactUsersViewModel by lazy {
        // Get View Model
        ViewModelProvider(this).get(ContactsViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_invites_users

    override val viewModel: BaseViewModel?
        get() = mContactUsersViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    private var mSectionList: ArrayList<Contact>? = null

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        tvToolbarTitle.text = context?.getString(R.string.invites_friends)
        flShimmer.startShimmer()
        flShimmer.visible()
        Handler().postDelayed( {
            mContactUsersViewModel.fetchContacts()
        },600)
       // mContactUsersViewModel.fetchContacts()
    }

    private fun setupListeners() {

    }

    private fun observeProperties() {
        mContactUsersViewModel.contactsLiveData.observe(this, Observer {
            flShimmer.stopShimmer()
            flShimmer.gone()
          //  mAdapter.submitList(it)
            recyclerView.setHasFixedSize(true)
            mAdapter.submitList(getHeaderListLatter(it))
        })
    }

    override fun onPullDownToRefresh() {

    }

    override fun onStop() {
        hideKeyboard()
        super.onStop()
    }

    override fun performInvitesUserClickAction(invitesUserPhoneNumber: String) {
        val uri: Uri = Uri.parse("smsto: $invitesUserPhoneNumber")
        val it = Intent(Intent.ACTION_VIEW, uri)
        it.putExtra("sms_body", "I’ve found this app and I think you’ll love it! Community, Brands and Restaurants, all 100% Vegan & Cruelty-Free. Tap: https://forthgreen.com/")
        it.putExtra("exit_on_sent", true);
        val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context)
        if (defaultSmsPackageName != null) {
            it.setPackage(defaultSmsPackageName)
        }
        startActivity(it)
    }

    private fun getHeaderListLatter(usersList: ArrayList<Contact>): List<Contact> {
        mSectionList = arrayListOf()
        usersList.sortWith(Comparator { user1, user2 ->
            user1!!.name.get(0).toUpperCase().compareTo(user2!!.name.get(0).toUpperCase())
        })

        var lastHeader: String? = ""
        val size: Int = usersList.size
        for (i in 0 until size) {
            val user: Contact = usersList[i]
            val header: String = java.lang.String.valueOf(user.name.get(0)).toUpperCase()
            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header
                if (user.numbers.size>0) {
                    mSectionList!!.add(Contact(header, header, true))
                }
              //  mSectionList!!.add(Contact(header, header, true))
            }
            if (user.numbers.size>0) {
                mSectionList!!.add(user)
            }
          //  mSectionList!!.add(user)
        }
        return mSectionList!!
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}