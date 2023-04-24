package com.forthgreen.app.views.dialogfragments

import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.forthgreen.app.R
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import kotlinx.android.synthetic.main.dialog_login_user.*


class UserLoginDialog {

    fun showUserLoginDialog(
        act: AppCompatActivity,
        loginButtonClickInterface: LoginButtonClickInterface,
    ) {
        MaterialDialog(act).show { customView(
            R.layout.dialog_login_user, dialogWrapContent = true, noVerticalPadding = true)
            cancelOnTouchOutside(false)
            cancelable(false)
            cornerRadius(res = R.dimen.dialog_corner_radius)

            tvCancel!!.setOnClickListener {
                dismiss()
            }
            tvLogin!!.setOnClickListener {
                dismiss()
                loginButtonClickInterface.loginButtonClick()
            }
        }
    }
}