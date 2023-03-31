package com.forthgreen.app.views.fragments

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.viewmodels.AccountUpdateViewModel
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.toolbar.*

class AccountFragment : BaseFragment() {

    companion object {
        const val TAG = "AccountFragment"
        const val LOCAL_INTENT_PROFILE_UPDATED = "profileUpdated"
    }

    private val mAccountUpdateViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(AccountUpdateViewModel::class.java)
    }
    private lateinit var user: UserProfile

    override val layoutId: Int
        get() = R.layout.fragment_account

    override val viewModel: BaseViewModel?
        get() = mAccountUpdateViewModel

    override fun init() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        //Set toolbar title
        tvToolbarTitle.text = getString(R.string.account)
        PushDownAnim.setPushDownAnimTo(tvChangePassword, btnUpdate)
        //getting previous registered info from sharedPref via viewModel
        user = mAccountUpdateViewModel.getUserDataFromSharedPrefs()
        etFull.setText("${user.firstName} ${user.lastName}")
        etEmail.setText(user.email)
    }

    private fun setupListeners() {
        tvChangePassword.setOnClickListener {
            performTransaction(ChangePasswordFragment(), ChangePasswordFragment.TAG)
        }
        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val mFullName = etFull.text.toString().trim()
        if (!etFull.verifyTextForErrors()) {
            etFull.error = getString(R.string.empty_field_error)
            etFull.requestFocus()
            return
        }
        //passing updated info by user to viewModel to update info at backend
        //mAccountUpdateViewModel.updateDetails(mFullName, null)
    }

    //observes value from viewModel
    private fun observeProperties() {
        mAccountUpdateViewModel.onUpdateSuccess().observe(viewLifecycleOwner, Observer {
            if (it) {
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_PROFILE_UPDATED))
                showMessage(message = "Profile Updated", isShowSnackbarMessage = false)
                supportFragmentManager().popBackStack()
            }
        })
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = false)
    }
}