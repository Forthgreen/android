package com.forthgreen.app.views.fragments

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.forthgreen.app.R
import com.forthgreen.app.repository.networkrequest.WebConstants
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.SignUpViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.utils.doOnTextChanged
import com.forthgreen.app.views.utils.setOnSafeClickListener
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.verifyTextForErrors
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment() {

    companion object {
        const val TAG = "SignUpFragment"
    }

    //Variables
    private val mSignUpViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_sign_up

    override val viewModel: BaseViewModel?
        get() = mSignUpViewModel

    override fun init() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        //Set PushDownAnim
        PushDownAnim.setPushDownAnimTo(btnSignUp)

        // Disable button
        disableButton()

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

    //Helper method to observe ViewModel
    private fun observeProperties() {
        mSignUpViewModel.onSuccessfulSignUp().observe(viewLifecycleOwner, Observer {
            showProgressDialog(false)
            if (it.code == 100) {
                //Shows dialog on successful Sign Up.
                MaterialDialog(requireContext()).show {
                    title(R.string.verification_title_dialog)
                    message(R.string.verify_message_dialog)
                    positiveButton(R.string.ok) { dialog ->
                        dialog.dismiss()
                        activity?.supportFragmentManager?.popBackStack()
                        performTransaction(ContinueWithEmailTabFragment.newInstance(true),
                            ContinueWithEmailTabFragment.TAG)
                    }
                    cancelable(false)
                    cancelOnTouchOutside(false)
                }
            } else {
                showMessage(message = it.message, isShowSnackbarMessage = true)
            }
        })
    }

    private fun setupListeners() {
//        viewGender.setOnClickListener {
//            //Set Up Drop Down List
//            val genderList = arrayListOf<String>()
//            genderList.add(getString(R.string.male_label))
//            genderList.add(getString(R.string.female_label))
//            genderList.add(getString(R.string.other_label))
//
//            showDropDownList(etGender, genderList)
//        }

//        viewDOB.setOnClickListener {
//            //Open Date Picker Dialog
//            val dob = SimpleDateFormat("dd/MM/yyyy")
//            MaterialDialog(requireContext()).show {
//                datePicker() { dialog, datetime ->
//                    val mDay = datetime.dayOfMonth
//                    val mMonth = datetime.month + 1 //Months start from zero
//                    val mYear = datetime.year
//                    this@SignUpFragment.etDOB.setText(dob.format(datetime.time))
//                    dialog.dismiss()
//                }
//            }
//        }
        etName.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                validateUserData(false)
            }
        }
        etName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomName.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomName.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etEmail.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                validateUserData(false)
            }
        }
        etEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomEmail.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomEmail.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etPass.doOnTextChanged { text, _, _, _ ->
            if (text.isNotEmpty()) {
                validateUserData(false)
            }
        }
        etPass.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomPassword.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomPassword.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        etUserName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                viewBottomUsername.setBackgroundResource(R.color.colorGreenDefaultButton)
            } else {
                viewBottomUsername.setBackgroundResource(R.color.colorEditTextBottomGrey)
            }
        }
        btnSignUp.setOnSafeClickListener {
            validateUserData(true)
        }
    }

    private fun validateUserData(hitAPI: Boolean = false) {
        val fullName = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val userName = etUserName.text.toString().trim()
        val password = etPass.text.toString().trim()

        if (!etName.verifyTextForErrors()) {
            disableButton()
            return
        }

        if (!etEmail.verifyTextForErrors()) {
            disableButton()
            return
        }

//        if (!etUserName.verifyTextForErrors()) {
//            return
//        }

        if (!etPass.verifyTextForErrors()) {
            disableButton()
            return
        }

        enableButton()

        if (hitAPI) {
            mSignUpViewModel.signUpUser(mFullName = fullName, mEmail = email,
                    mUserName = userName, mPassword = password)
        }
    }

    /**
     * @description Show Drop Down List on Textview with the given list data
     * @param mTextView textview on which to show list
     * @param mDataList list of data in strings to show in the drop down list.
     */
    private fun showDropDownList(mTextView: TextView, mDataList: List<String>) {
        //Create Popup Menu and assign adapter to it with data list data
        val listPopupWindow = ListPopupWindow(requireContext())
        listPopupWindow.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mDataList))

        //Assign the list to the given text view
        listPopupWindow.anchorView = mTextView
        listPopupWindow.isModal = true
        listPopupWindow.setOnItemClickListener { _, _, position, _ ->
            listPopupWindow.dismiss()
            mTextView.text = mDataList[position]
        }
        listPopupWindow.show()
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer, frag, fragmentTag, isAddFragment = true)
    }

    private fun disableButton() {
        // Disable button
        btnSignUp.apply {
            isEnabled = false
            setBackgroundResource(R.color.colorTabGreyBG)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorEditTextBottomGrey))
        }
    }

    private fun enableButton() {
        // Enable button
        btnSignUp.apply {
            isEnabled = true
            setBackgroundResource(R.color.colorGreenDefaultButton)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        }
    }

}
