package com.forthgreen.app.views.fragments

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.AccountUpdateViewModel
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SettingsViewModel
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_delete_account.*
import kotlinx.android.synthetic.main.dialog_delete_account.tvCancel
import kotlinx.android.synthetic.main.dialog_delete_delete_account_user.*
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File

/**
 * @author Nandita Gandhi
 * @since 10-04-2021
 */
class EditProfileFragment : BasePictureOptionsFragment() {

    companion object {
        const val TAG = "EditProfileFragment"
        const val LOCAL_INTENT_PROFILE_UPDATED = "LOCAL_INTENT_PROFILE_UPDATED"
    }

    // Variables
    private var mImageFile: File? = null

    private val mUpdateProfileViewModel by lazy {
        ViewModelProvider(this).get(AccountUpdateViewModel::class.java)
    }

    private val userProfile: UserProfile
        get() = mUpdateProfileViewModel.getUserDataFromSharedPrefs()

    override val layoutId: Int
        get() = R.layout.fragment_edit_profile

    override val viewModel: BaseViewModel?
        get() = mUpdateProfileViewModel

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Setup toolbar
        tvToolbarTitle.text = getString(R.string.edit_profile_toolbar_title)

        // Push down anim
        PushDownAnim.setPushDownAnimTo(btnUpdate, tvChangePassword, tvPasswordLabel, tvDeleteAccountLabel)

        // Setup details which are there.
        mUpdateProfileViewModel.getUserProfileDataFromSharedPrefs().apply {
            civUserImage.loadURL(image, true)
            etName.setText(firstName)
            etUserName.setText(username)
            etBio.setText(bio)
            etEmail.setText(email)
            if (socialIdentifier == ValueMapping.onSocialLoginFacebook() ||
                socialIdentifier == ValueMapping.onSocialLoginGoogle()
            ) {
                groupPassword.gone()
            } else {
                groupPassword.visible()
            }
        }
        // Disable button
        disableButton()
    }

    private fun setupListeners() {
        etName.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomName.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomName.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etUserName.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etUserName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomUsername.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomUsername.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etBio.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etBio.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomBio.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomBio.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        tvPasswordLabel.setOnClickListener {
            performFragTransaction(ChangePasswordFragment(), ChangePasswordFragment.TAG)
        }

        tvDeleteAccountLabel.setOnClickListener {
            performDeleteAccount()
        }
        civUserImage.setOnClickListener {
            showPictureOptionsBottomSheet(
                true,
                Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS,
                false
            )
        }
        btnUpdate.setOnClickListener {
            verifyDetails(true)
        }
    }

    private fun performDeleteAccount() {
        //Show Custom Dialog by inflating Layout using MaterialDialog Library.
        MaterialDialog(requireContext()).show {
            customView(
                R.layout.dialog_delete_delete_account_user,
                dialogWrapContent = true,
                noVerticalPadding = true
            )
            cancelOnTouchOutside(false)
            cancelable(false)
            cornerRadius(res = R.dimen.dialog_corner_radius)
            tv_yes.setOnClickListener {
                this.dismiss()
                //Show Dialog in case of Guest mode else perform action.
                mUpdateProfileViewModel.deleteAccount()
            }
            tv_cancel.setOnClickListener {
                this.dismiss()
            }
        }
    }

    private fun verifyDetails(apiHit: Boolean) {
        val name = etName.trimmedText
        val username = etUserName.trimmedText
        val email = etEmail.trimmedText
        val bio = etBio.trimmedText

        // Verify the required details if empty
        if (!etName.verifyTextForErrors()) {
            if (apiHit) {
                etName.showError(R.string.empty_field_error)
            }
            disableButton()
            return
        }
        if (!etUserName.verifyTextForErrors()) {
            if (apiHit) {
                etUserName.showError(R.string.empty_field_error)
            }
            disableButton()
            return
        }
        if (!etBio.verifyTextForErrors()) {
            if (apiHit) {
                etBio.showError(R.string.invalid_email_error)
            }
            disableButton()
            return
        }

        if (name == userProfile.firstName && username == userProfile.username && bio == userProfile.bio && mImageFile == null) {
            disableButton()
            return
        }
        enableButton()

        if (apiHit) {
            // Update the details
            mUpdateProfileViewModel.updateDetails(name, username, bio, mImageFile)
        }
    }

    private fun observeProperties() {
        mUpdateProfileViewModel.onUpdateSuccess().observe(viewLifecycleOwner, { profileUpdated ->
            if (profileUpdated) {
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(LOCAL_INTENT_PROFILE_UPDATED))
                showMessage(resId = R.string.update_success)
                supportFragmentManager().popBackStack()
            }
        })

        mUpdateProfileViewModel.onDeleteAccountSuccess().observe(viewLifecycleOwner, { accountDeleted ->
            if (accountDeleted) {
                Log.d("editprofileAccount", "true")
                navigateToMainActivity()
            }
        })
    }

    private fun enableButton() {
        // Enable button
        btnUpdate.apply {
            setBackgroundResource(R.color.colorGreenDefaultButton)
            isEnabled = true
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        }
    }

    private fun disableButton() {
        btnUpdate.apply {
            setBackgroundResource(R.color.colorTabGreyBG)
            isEnabled = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorEditTextBottomGrey))
        }
    }

    override fun onGettingImageFile(file: File) {
        mImageFile?.delete()
        mImageFile = file
        verifyDetails(false)
        Glide.with(this)
            .load(Constants.LOCAL_FILE_PREFIX + mImageFile)
            .into(civUserImage)
    }

    override fun onGettingVideoUri(uri: Uri, type: Int) {
    }
}