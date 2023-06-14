package com.forthgreen.app.views.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.forthgreen.app.R
import com.forthgreen.app.repository.networkrequest.WebConstants
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.WelcomeViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.MainActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.supportFragmentManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.json.JSONObject


class WelcomeFragment : BaseFragment() {

    companion object {
        const val TAG = "Welcome"
        private const val GOOGLE_SIGN_IN = 666

        private const val BUNDLE_EXTRAS_IS_FROM_MY_BRANDS = "isFromMyBrands"

        fun newInstance(isFromMyBrands: Boolean): WelcomeFragment {
            return WelcomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(BUNDLE_EXTRAS_IS_FROM_MY_BRANDS, isFromMyBrands)
                }
            }
        }
    }

    //Variables
    private val mWelcomeViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(WelcomeViewModel::class.java)
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

    //CallbackManager to handle login responses for Facebook SDK
    private val mCallbackManager by lazy {
        CallbackManager.Factory.create()
    }
    private val mLoginManager by lazy { LoginManager.getInstance() }
    private val isFromMyBrands by lazy { arguments!!.getBoolean(BUNDLE_EXTRAS_IS_FROM_MY_BRANDS, false) }

    override val layoutId: Int
        get() = R.layout.fragment_welcome

    override val viewModel: BaseViewModel?
        get() = mWelcomeViewModel

    override fun init() {
        setUpViews()
        setClickListeners()
        observeProperties()
    }

    private fun setUpViews() {
        PushDownAnim.setPushDownAnimTo(btnEmail, btnFacebook, btnGoogle)

        // hide close icon if fragment is displayed in my brands
//        if (isFromMyBrands) ivBackButton.gone() else ivBackButton.visible()

       // if (!isFromMyBrands) ivBackButton.gone() else ivBackButton.visible()

        /**
         *  methods for making terms and conditions policy
         */
        val termString = getString(R.string.terms)
        val termStartIndex = termString.indexOf(getString(R.string.term_spannable))
        val termEndIndex = termStartIndex + getString(R.string.term_spannable).length
        val spannableTerm = SpannableString(termString)


        val termClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(v: View) {
                GeneralFunctions.openLinkInBrowser(requireContext(), WebConstants.EXTERNAL_LINK_FOR_TERMS_AND_CONDITIONS)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        spannableTerm.setSpan(termClickableSpan, termStartIndex, termEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        /**
         *  methods for making privacy policy
         */
        val privacyStartIndex = termString.indexOf(getString(R.string.privacy_spannable))
        val privacyEndIndex = privacyStartIndex + getString(R.string.privacy_spannable).length


        val privacyClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(v: View) {
                GeneralFunctions.openLinkInBrowser(requireContext(), WebConstants.EXTERNAL_LINK_FOR_PRIVACY_AND_POLICY)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        spannableTerm.setSpan(privacyClickableSpan, privacyStartIndex, privacyEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTerms.text = spannableTerm
        tvTerms.movementMethod = LinkMovementMethod.getInstance()
        tvTerms.highlightColor = Color.TRANSPARENT
    }

    private fun setClickListeners() {
        ivBackButton.setOnClickListener {
            supportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        tvSkipLabel.setOnClickListener {
            supportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        btnEmail.setOnClickListener {
            performFragTransaction(
                ContinueWithEmailTabFragment.newInstance(false), ContinueWithEmailTabFragment.TAG,
                enterAnim = R.anim.slide_in_right_medium, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )
        }

//        tvGuest.setOnClickListener {
//            mWelcomeViewModel.updateLoggedInStatus(ValueMapping.getUserAccessGuest())
//            startActivity(Intent(requireContext(), MainActivity::class.java))
//            activity?.finish()
//        }
        btnFacebook.setOnClickListener { performFacebookSignIn() }
        btnGoogle.setOnClickListener { performGoogleSignIn() }

    }

    //Helper method to observe data via ViewModel
    private fun observeProperties() {
        mWelcomeViewModel.onSuccessfulSignIn().observe(viewLifecycleOwner, Observer { socialLoginSuccess ->
            showProgressDialog(false)
            if (socialLoginSuccess) {
                //Change Activity on Successful Login
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
        })
    }


    private fun performGoogleSignIn() {
        //Check if a user has already signed in to your app with Google
        val mAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (mAccount != null) {
            loginViaGoogle(mAccount)
        } else {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun performFacebookSignIn() {

        mLoginManager.logInWithReadPermissions(this, listOf("public_profile", "email"))

        mLoginManager.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                //Use Facebook Graph API to get user data
                val request = GraphRequest.newMeRequest(result?.accessToken) { jsonObject, _ ->
                    loginViaFacebook(jsonObject!!, result?.accessToken?.userId!!, result?.accessToken?.token!!)
                }
                request.apply {
                    parameters = Bundle().apply {
                        putString("fields", "id, email, first_name, last_name")
                    }
                }.executeAsync()
            }

            override fun onCancel() {
                showMessage(R.string.retrofit_failure)
            }

            override fun onError(error: FacebookException?) {
                if (error != null) {
                    showMessage(message = error.message)
                } else {
                    showMessage(R.string.retrofit_failure)
                }
            }
        })
    }

    //Helper method to hit backend API with received google Info.
    private fun loginViaGoogle(mAccount: GoogleSignInAccount?) {
        if (mAccount != null) {
            val mFirstName = mAccount.givenName!!
            val lastName = mAccount.familyName
            var mLastName:String = ""
            if (lastName != null) {
                 mLastName = lastName
            }
            val mEmail = mAccount.email!!
          //  val mPicture = mAccount.photoUrl.toString()
            val mSocialID = mAccount.id!!
            val mSocialToken = mAccount.idToken!!

            mWelcomeViewModel.userSocialLogin(mFirstName + mLastName, mEmail, ValueMapping.getTypeOther(), "", mSocialID, mSocialToken, ValueMapping.onSocialLoginGoogle(), null)
        }
    }

    //Helper method to hit backend API with received google Info.
    private fun loginViaFacebook(mJSONObject: JSONObject, mSocialID: String, mSocialToken: String) {
        val mFirstName = mJSONObject.getString("first_name")
        val mLastName = mJSONObject.getString("last_name")
        var mEmail: String = ""
        if (mJSONObject.has("email")) {
            mEmail = mJSONObject.getString("email")
        }
       // val mEmail = mJSONObject.getString("email")
      //  val mPicture = ImageRequest.getProfilePictureUri(mSocialID, 500, 500).toString()


        mWelcomeViewModel.userSocialLogin(mFirstName + mLastName, mEmail, ValueMapping.getTypeOther(), "", mSocialID, mSocialToken, ValueMapping.onSocialLoginFacebook(), null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, show authenticated UI.
                val mAccount = completedTask.getResult(ApiException::class.java)
                loginViaGoogle(mAccount)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("exception", "$e")
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }

}