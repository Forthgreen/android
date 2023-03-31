package com.forthgreen.app.views.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.forthgreen.app.R
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.google.android.material.textfield.TextInputEditText


/**
 * Created by Chetan Tuteja on 2019-11-27.
 */

/**
 * @description Helper method to make text view Spannable.
 *
 *
 * @param str {String} part of string on which to perform change.
 * @param underlined {Boolean} whether the text should be underlined.
 * @param bold {Boolean} whether the text should be bold.
 * @param action {Lambda} Action to be performed onClick.
 */
fun TextView.makeTextLink(
        str: String, underlined: Boolean, bold: Boolean,
        @ColorRes color: Int? = null, action: (() -> Unit)? = null,
) {

    // Make Current Text of Text view as Spannable.
    val spannableString = SpannableString(this.text)

    // Assign text color as current text color or the give color.
    val textColor = color?.let { colorRes ->
        ContextCompat.getColor(this.context, colorRes)
    } ?: this.currentTextColor

    // Make Clickable Span
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            //Perform action if there.
            action?.invoke()
        }

        override fun updateDrawState(drawState: TextPaint) {
            super.updateDrawState(drawState)
            //Assign text color and underline.
            drawState.isUnderlineText = underlined
            drawState.color = textColor
        }
    }
    // Index of the given string in the whole string, return if index not found i.e less than 0.
    val index = spannableString.indexOf(str, ignoreCase = true)
    if (index < 0) {
        return
    }

    if (bold) {
        // If Bold, make it bold.
        spannableString.setSpan(StyleSpan(Typeface.BOLD), index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    // Set Span and Assign to tex view.
    spannableString.setSpan(clickableSpan, index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannableString
    this.movementMethod = LinkMovementMethod.getInstance()
    this.highlightColor = Color.TRANSPARENT
}

/*Sets portion of a textview as spannble to change its color.
Takes in the text to set as spannable and color of the text as input.*/
fun TextView.setSpannableColor(spannablePart: String, color: Int) {
    //Set Up Spannable
    val completeString = SpannableString(this.text.toString())
    val startSpan = completeString.toString().indexOf(string = spannablePart, ignoreCase = true)
    val endSpan = startSpan + spannablePart.length

    completeString.setSpan(ForegroundColorSpan(color), startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.setText(completeString, TextView.BufferType.SPANNABLE)
}

/**
 *  @description Lambda Extension for Adding Text Change Listener to EditText.
 *               Adds TextChangeListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun EditText.doOnTextChanged(crossinline action: (text: String, start: Int, before: Int, count: Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
            //Invokes the Lambda function if the char is not null.
            char?.let { text ->
                action.invoke(text.toString(), start, before, count)
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}

/**
 *  @description Lambda Extension for Adding Text Change Listener to EditText.
 *               Adds TextChangeListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun EditText.doBeforeTextChanged(crossinline action: (text: String, start: Int, count: Int, after: Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(char: CharSequence?, start: Int, count: Int, after: Int) {
            //Invokes the Lambda function if the char is not null.
            char?.let { text ->
                action.invoke(text.toString(), start, count, after)
            }
        }

        override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}

/**
 *  @description Lambda Extension for Adding Text Change Listener to EditText.
 *               Adds TextChangeListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun EditText.doAfterTextChanged(crossinline action: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            //Invokes the Lambda function if the Editable is not null.
            editable?.let { text ->
                action.invoke(text.toString())
            }
        }
    })
}

//Set view visibility as visible
fun View.visible() {
    this.visibility = View.VISIBLE
}

//Set view visibility as gone
fun View.gone() {
    this.visibility = View.GONE
}

fun View.setVisibility(visible: Boolean) {
    this.visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

//Hide keyboard in a fragment
fun Fragment.hideKeyboard() {
    (this.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(this.view?.rootView?.windowToken, 0)
}

//Show keyboard in a fragment
fun Fragment.showKeyboard() {
    (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY)
}

//To provide with Fragment Manager via Activity
fun Fragment.supportFragmentManager(): FragmentManager {
    return (this.activity as BaseAppCompactActivity)?.supportFragmentManager
}

//Converts Fragment to a full Screen One
fun Fragment.makeFragmentFullScreen() {
    if (activity != null) {
        activity!!.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorTransparent)
        activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

//Converts fragment to normal size.
fun Fragment.makeFragmentNormalSize(isStatusBarWhite: Boolean) {
    //Returns status bar to primary color for other fragments.
    if (isStatusBarWhite) {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),
                R.color.colorWhite)
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),
                R.color.colorPrimaryDark)
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}

/*Extension function to check the EditText for errors based on its input type.
Returns true if the text is valid else returns false*/
fun EditText.verifyTextForErrors(): Boolean {
    return when (this.inputType - 1) {          //The XML Input type Value and Java Value differs by 1, thus 1 is subtracted.
        InputType.TYPE_TEXT_VARIATION_PASSWORD, InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> {
            GeneralFunctions.isValidPassword(this.text.toString().trim())
        }
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> {
            GeneralFunctions.isValidEmail(this.text.toString().trim())
        }
        else -> {
            //Returns the opposite in this case since if the text is not empty then it is valid.
            !this.text.toString().trim().isNullOrEmpty()
        }
    }
}

/*Extension function to check the EditText for errors based on its input type.
Returns true if the text is valid else returns false*/
fun TextInputEditText.verifyTextForErrors(): Boolean {
    return when (this.inputType - 1) {          //The XML Input type Value and Java Value differs by 1, thus 1 is subtracted.
        InputType.TYPE_TEXT_VARIATION_PASSWORD, InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> {
            GeneralFunctions.isValidPassword(this.text.toString().trim())
        }
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> {
            GeneralFunctions.isValidEmail(this.text.toString().trim())
        }
        else -> {
            //Returns the opposite in this case since if the text is not empty then it is valid.
            !this.text.toString().trim().isNullOrEmpty()
        }
    }
}

/** Returns trimmed text for Edit text and TextView. */
val TextView.trimmedText: String
    get() = this.text.toString().trim()

/**
 *  @description Displays Error for a specific EditText using the StringRes or Text Message.
 *
 *
 *  @param text {String} String message to be shown (if null method will use resId to show message)
 *  @param resId {Int?} resource id is string (will be used if message value is null)
 */
fun EditText.showError(
        @StringRes resId: Int? = null,
        text: String? = null,
) {
    if (resId == null && text == null) {
        //Do Nothing if both are null.
    } else {
        this.error = text ?: context.getString(resId!!)
        this.requestFocus()
    }
}

/*Extension to load Image into ImageView directly via Glide.
If isUserImage is true, load Avatar as Placeholder and Error, else loads generic Image*/
fun ImageView.loadURL(imageURL: String, isUserImage: Boolean, cornerRadius: Int = 0) {
    //Get complete Image URL using Identifier received.
    val minURL = GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), imageURL)
    val bestURL = GeneralFunctions.getResizedImage(ValueMapping.getPathBest(), imageURL)

    // Create Progress Drawable to use as placeholder.
    val progressDrawable = CircularProgressDrawable(context).apply {
        setColorSchemeColors(R.color.colorAccent)
        centerRadius = 18f
        strokeWidth = 1f
        start()
    }


    if (isUserImage) {
        Glide.with(this)
                .load(bestURL)
                .thumbnail(Glide.with(this).load(minURL))
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(this)
    } else {
        if (cornerRadius > 0)
            Glide.with(this)
                    .load(bestURL)
                    .thumbnail(Glide.with(this).load(minURL))
                    .placeholder(progressDrawable)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .error(Glide.with(this)
                            .load(R.drawable.ic_placeholder_broken_img)
                            .transform(CenterCrop(), RoundedCorners(cornerRadius)))
                    .error(R.drawable.ic_placeholder_broken_img)
                    .into(this)
        else
            Glide.with(this)
                    .load(bestURL)
                    .thumbnail(Glide.with(this).load(minURL))
                    .placeholder(progressDrawable)
                    .error(R.drawable.ic_placeholder_broken_img)
                    .into(this)
    }
}

/** Extension function to return the first element or an empty string instead. **/
fun List<String>.firstOrEmpty() = this.firstOrNull() ?: ""
