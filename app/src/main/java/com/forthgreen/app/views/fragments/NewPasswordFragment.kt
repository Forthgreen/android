package com.forthgreen.app.views.fragments

import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_new_password.*
import kotlinx.android.synthetic.main.toolbar.*

class NewPasswordFragment : BaseFragment() {
    companion object {
        const val TAG = "New Password"
    }

    override val layoutId: Int
        get() = R.layout.fragment_new_password

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        tvToolbarTitle.text = getString(R.string.toolbar_new_password)
        PushDownAnim.setPushDownAnimTo(btnSave)
    }

}
