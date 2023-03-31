package com.forthgreen.app.views.fragments

import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.AccountUpdateViewModel
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.utils.doOnTextChanged
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.toolbar.*

class ChangePasswordFragment : BaseFragment() {

    companion object {
        const val TAG = "ChangePasswordFragment"
    }

    private val mAccountUpdateViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(AccountUpdateViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_change_password

    override val viewModel: BaseViewModel?
        get() = mAccountUpdateViewModel

    override fun init() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        //Set toolbar title
        tvToolbarTitle.text = getString(R.string.change_password)
        PushDownAnim.setPushDownAnimTo(btnChangePassword)

        // Disable button
        disableButton()
    }

    private fun setupListeners() {
        etCurrentPass.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etCurrentPass.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomCurrentPass.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomCurrentPass.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etPass.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etPass.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomPass.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomPass.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etConfirmPass.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                verifyDetails(false)
            }
        }
        etConfirmPass.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomConfirmPass.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomConfirmPass.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        btnChangePassword.setOnClickListener {
            verifyDetails(true)
        }
    }

    private fun verifyDetails(apiHit: Boolean) {
        if (!etCurrentPass.verifyTextForErrors()) {
            disableButton()
            return
        }
        if (!etPass.verifyTextForErrors()) {
            disableButton()
            return
        }
        if (etConfirmPass.text.toString().trim() != etPass.text.toString().trim()) {
            disableButton()
            return
        }

        enableButton()

        if (apiHit) {
            mAccountUpdateViewModel.updatePassword(
                etCurrentPass.text.toString().trim(),
                etPass.text.toString().trim()
            )
        }

    }

    private fun observeProperties() {
        mAccountUpdateViewModel.onUpdatePass().observe(viewLifecycleOwner, Observer {
            if (it) {
                showMessage(resId = R.string.password_updated, isShowSnackbarMessage = false)
                supportFragmentManager().popBackStack()
            }
        })
    }

    private fun enableButton() {
        // Enable button
        btnChangePassword.apply {
            setBackgroundResource(R.color.colorGreenDefaultButton)
            isEnabled = true
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        }
    }

    private fun disableButton() {
        btnChangePassword.apply {
            setBackgroundResource(R.color.colorTabGreyBG)
            isEnabled = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorEditTextBottomGrey))
        }
    }
}