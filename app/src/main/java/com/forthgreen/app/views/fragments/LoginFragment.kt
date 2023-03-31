package com.forthgreen.app.views.fragments

import android.content.Intent
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SignInViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.MainActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.doOnTextChanged
import com.forthgreen.app.views.utils.setOnSafeClickListener
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    // Variables
    private val mSignInViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(SignInViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_login

    override val viewModel: BaseViewModel?
        get() = mSignInViewModel

    override fun init() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        //Set PushDownAnim
        PushDownAnim.setPushDownAnimTo(btnLogin, tvForgot)

        tvForgot.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // Disable button
        disableButton()
    }

    //Helper method to observe ViewModel
    private fun observeProperties() {
        mSignInViewModel.onSuccessfulSignIp().observe(viewLifecycleOwner, Observer {
            showProgressDialog(false)
            if (it.code == 100) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            } else {
                showMessage(message = it.message, isShowSnackbarMessage = true)
            }
        })
    }

    private fun setupListeners() {
        tvForgot.setOnClickListener {
            performFragTransaction(
                ForgotPasswordFragment(), ForgotPasswordFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )
        }
        etEmail.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomEmail.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomEmail.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etPassword.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etPassword.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomPass.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomPass.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        btnLogin.setOnSafeClickListener {
            verifyDetails(true)
        }
    }

    private fun verifyDetails(hitAPI: Boolean = false) {
        val email = etEmail.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (!etEmail.verifyTextForErrors()) {
            disableButton()
            return
        }
        if (!etPassword.verifyTextForErrors()) {
            disableButton()
            return
        }

        enableButton()

        if (hitAPI) {
            mSignInViewModel.userSignIn(email, pass)
        }
    }

    private fun disableButton() {
        btnLogin.apply {
            setBackgroundResource(R.color.colorTabGreyBG)
            isEnabled = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorEditTextBottomGrey))
        }
    }

    private fun enableButton() {
        // Enable button
        btnLogin.apply {
            setBackgroundResource(R.color.colorGreenDefaultButton)
            isEnabled = true
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        }
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = false)
    }

}