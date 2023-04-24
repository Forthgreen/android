package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.forthgreen.app.R
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SettingsViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File

class SettingsFragment : BasePictureOptionsFragment(), View.OnClickListener {

    companion object {
        const val TAG = "SignedInUser"
    }

    //Variables
    private var mImageFile: File? = null
    private val mSettingsViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }

    private val mGoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
    }

    private val mGoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_settings

    override val viewModel: BaseViewModel?
        get() = mSettingsViewModel

    override fun setData() {

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        PushDownAnim.setPushDownAnimTo(ivUserImage)

        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            groupLoggedIn.visible()
            groupGuest.gone()
        } else {
            groupLoggedIn.gone()
            groupGuest.visible()
        }

        val userProfile = mSettingsViewModel.getUserProfile()
        if (userProfile == null)
            tvUser.text = "Guest"
        else {
            ivUserImage.loadURL(userProfile.image, true)
            tvUser.text = "${userProfile.firstName} ${userProfile.lastName}"
        }
    }

    private fun setupListeners() {
        ivUserImage.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
        tvUserAccount.setOnClickListener(this)
        tvReviews.setOnClickListener(this)
        tvContact.setOnClickListener(this)
        tvTermsConditions.setOnClickListener(this)
        tvPrivacyPolicy.setOnClickListener(this)
        tvLogout.setOnClickListener(this)

        //Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, IntentFilter(AccountFragment.LOCAL_INTENT_PROFILE_UPDATED))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivUserImage -> {
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn())
                    showPictureOptionsBottomSheet(true, Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS, false)
            }
            R.id.tvLogin -> {
               // performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                callUserLoginDialog()
            }
            R.id.tvUserAccount -> {
                performTransaction(AccountFragment(), AccountFragment.TAG)
            }
            R.id.tvReviews -> {
                performTransaction(MyReviewsFragment(), MyReviewsFragment.TAG)
            }
            R.id.tvContact -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SENDTO
                    data = Uri.parse("mailto:")     // Only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.contact_us_email)))
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            R.id.tvTermsConditions -> {
                performTransaction(StaticDataFragment.newInstance(ValueMapping.onStaticDataTerms()), StaticDataFragment.TAG)
            }
            R.id.tvPrivacyPolicy -> {
                performTransaction(StaticDataFragment.newInstance(ValueMapping.onStaticDataPrivacyPolicy()), StaticDataFragment.TAG)
            }
            R.id.tvLogout -> {
                performLogout()
            }
        }
    }

    override fun onGettingImageFile(file: File) {
        mImageFile?.delete()
        mImageFile = file
        Glide.with(this)
                .load(Constants.LOCAL_FILE_PREFIX + mImageFile)
                .into(ivUserImage)
        mSettingsViewModel.getUserProfile()?.let {
            //  mSettingsViewModel.updateDetails("${it.firstName} ${it.lastName}", mImageFile)
        }
    }


    //Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                setupViews()
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

    override fun onGettingVideoUri(uri: Uri, type: Int) {
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
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
}