package com.forthgreen.app.views.fragments

import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ForgotPasswordViewModel
import com.forthgreen.app.views.utils.doOnTextChanged
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.toolbar.*

class ForgotPasswordFragment : BaseFragment() {

    companion object {
        const val TAG = "Forgot Password"
    }

    //Variables
    private val mForgotPasswordViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_forgot_password

    override val viewModel: BaseViewModel?
        get() = mForgotPasswordViewModel

    override fun init() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    //Helper method to observe ViewModel
    private fun observeProperties() {
        mForgotPasswordViewModel.onSuccessfulApiResponse().observe(viewLifecycleOwner, Observer {
            showProgressDialog(false)
            showMessage(message = it.message, isShowSnackbarMessage = true)
            if (it.code == 100) {
                supportFragmentManager().popBackStack()
            }
        })
    }

    private fun setupViews() {
        //Set Toolbar title
        tvToolbarTitle.text = getString(R.string.forgot_password)

        //Set PushDownAnim
        PushDownAnim.setPushDownAnimTo(btnNewPassword)

        // Disable button
        disableButton()
    }

    private fun showSuccessDialog() {
        MaterialDialog(requireContext()).show {
            cancelable(false)
            cancelOnTouchOutside(true)
            title(text = "Email sent")
            message(text = "A link to reset password has been sent to you registered email")
            positiveButton(text = "close") {
                repeat(1) {
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }
    }

    private fun setupListeners() {
        etEmail.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomEt.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomEt.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        btnNewPassword.setOnClickListener {
            verifyDetails(true)
        }
    }

    private fun verifyDetails(apiHit: Boolean) {
        val email = etEmail.text.toString().trim()

        if (!etEmail.verifyTextForErrors()) {
            disableButton()
            return
        }
        enableButton()
        if (apiHit) {
            mForgotPasswordViewModel.forgotPassword(email)
        }
    }

    private fun enableButton() {
        // Enable button
        btnNewPassword.apply {
            setBackgroundResource(R.color.colorGreenDefaultButton)
            isEnabled = true
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        }
    }

    private fun disableButton() {
        btnNewPassword.apply {
            setBackgroundResource(R.color.colorTabGreyBG)
            isEnabled = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorEditTextBottomGrey))
        }
    }

}
