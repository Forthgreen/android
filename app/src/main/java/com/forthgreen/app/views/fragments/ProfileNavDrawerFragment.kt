package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.facebook.login.LoginManager
import com.forthgreen.app.R
import com.forthgreen.app.repository.networkrequest.WebConstants
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SettingsViewModel
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.loadURL
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_delete_account.*
import kotlinx.android.synthetic.main.dialog_product_detail_menu.*
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.dialog_review_menu.tvCancel
import kotlinx.android.synthetic.main.fragment_profile_nav_drawer.*

/**
 * @since Nandita Gandhi
 * @author 15-04-2021
 */
class ProfileNavDrawerFragment : BaseFragment(), View.OnClickListener {

    companion object {
        const val TAG = "ProfileNavFragment"
    }

    // Variables
    private val mSettingsViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }
    private val userProfile
        get() = mSettingsViewModel.getUserProfileDataFromSharedPrefs()

    private val mGoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
    }
    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private val mGoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for Drawer
        return inflater.inflate(R.layout.fragment_profile_nav_drawer, container, false)
    }

    override val layoutId: Int
        get() = R.layout.fragment_profile_nav_drawer

    override val viewModel: BaseViewModel?
        get() = mSettingsViewModel

    override fun init() {
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        tvUserFullName.text = userProfile.firstName
        civUserImage.loadURL(userProfile.image, true)


        // Push down anim
        PushDownAnim.setPushDownAnimTo(ivClose, tvEditProfile, tvContactUs, tvAboutUs, tvTermsConditions, tvPrivacyPolicy, tvLogout, tvDeleteAccount)

        // Receive broadcast
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, IntentFilter(EditProfileFragment.LOCAL_INTENT_PROFILE_UPDATED))
    }

    private fun setupListeners() {
        ivClose.setOnClickListener(this)
        tvEditProfile.setOnClickListener(this)
        tvContactUs.setOnClickListener(this)
        tvLeaveFeedback.setOnClickListener(this)
        tvAboutUs.setOnClickListener(this)
        tvTermsConditions.setOnClickListener(this)
        tvPrivacyPolicy.setOnClickListener(this)
        tvLogout.setOnClickListener(this)
        tvDeleteAccount.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                drawerCallbacks.closeNavDrawer()
            }
            R.id.tvEditProfile -> {
                drawerCallbacks.closeNavDrawer()
                performFragTransaction(EditProfileFragment(), EditProfileFragment.TAG)
            }
            R.id.tvContactUs -> {
                drawerCallbacks.closeNavDrawer()
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SENDTO
                    data = Uri.parse("mailto:")     // Only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.contact_us_email)))
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            R.id.tvLeaveFeedback -> {
                drawerCallbacks.closeNavDrawer()
                GeneralFunctions.openLinkInBrowser(
                    requireContext(),
                    WebConstants.EXTERNAL_LINK_FOR_LEAVE_FEEDBACK
                )
            }
            R.id.tvAboutUs -> {
                drawerCallbacks.closeNavDrawer()
                GeneralFunctions.openLinkInBrowser(
                    requireContext(),
                    WebConstants.EXTERNAL_LINK_FOR_ABOUT_US
                )
            }
            R.id.tvTermsConditions -> {
                drawerCallbacks.closeNavDrawer()
                GeneralFunctions.openLinkInBrowser(
                    requireContext(),
                    WebConstants.EXTERNAL_LINK_FOR_TERMS_AND_CONDITIONS
                )
            }
            R.id.tvPrivacyPolicy -> {
                drawerCallbacks.closeNavDrawer()
                GeneralFunctions.openLinkInBrowser(requireContext(), WebConstants.EXTERNAL_LINK_FOR_PRIVACY_AND_POLICY)
            }
            R.id.tvLogout -> {
                drawerCallbacks.closeNavDrawer()
                performLogout()
            }
            R.id.tvDeleteAccount -> {
                drawerCallbacks.closeNavDrawer()
                performDeleteAccount()
            }
        }
    }

    private fun performLogout() {
        try {
            mGoogleSignInClient.signOut()
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mSettingsViewModel.logout()
    }

    private fun performDeleteAccount() {
        //Show Custom Dialog by inflating Layout using MaterialDialog Library.
        MaterialDialog(requireContext()).show {
            customView(
                R.layout.dialog_delete_account,
                dialogWrapContent = true,
                noVerticalPadding = true
            )
            cancelOnTouchOutside(false)
            cancelable(false)
            cornerRadius(res = R.dimen.dialog_corner_radius)
            tvOk.setOnClickListener {
                this.dismiss()
                //Show Dialog in case of Guest mode else perform action.
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                    showGuestModeDialog()
                } else {
                   // mSettingsViewModel.deleteAccount()
                }
            }
            tvCancel.setOnClickListener {
                this.dismiss()
            }
        }
    }

    private fun showGuestModeDialog() {
       // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
        callUserLoginDialog()
    }

    private fun callUserLoginDialog() {
        val userLoginDialog = UserLoginDialog()
        userLoginDialog.showUserLoginDialog(requireActivity() as AppCompatActivity, object :
            LoginButtonClickInterface {
            override fun loginButtonClick() {
                performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            }
        })
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                setupViews()
            }
        }
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroyView()
    }
}