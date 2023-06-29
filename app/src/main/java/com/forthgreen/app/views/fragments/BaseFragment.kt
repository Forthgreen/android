package com.forthgreen.app.views.fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.forthgreen.app.R
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.MyCustomLoader
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.MainActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.DrawerCallbacks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by ShrayChona on 03-10-2018.
 * @description extend this class for basic fragment setup
 */
abstract class BaseFragment : androidx.fragment.app.Fragment() {

    private val mMyCustomLoader: MyCustomLoader by lazy { MyCustomLoader(context) }

    internal lateinit var drawerCallbacks: DrawerCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? =
            container?.inflate(layoutRes = layoutId)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (null != toolbar) {
            toolbar.setNavigationIcon(R.drawable.ic_back)
            toolbar.setNavigationOnClickListener {
                // Check if child stack has anything.
                val parentFrag = parentFragment
                if (parentFrag != null && parentFrag.childFragmentManager.backStackEntryCount > 0) {
                    parentFrag.childFragmentManager.popBackStack()
                } else {
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }
        init()
        observeBaseProperties()
    }

    val activityContext: Context
        get() = activity!!

    /**
     *  @description call this method to show toast or snack bars
     *  @param message {String} String message to be shown (if null method will use resId to show text)
     *  @param resId {Int?} resource id is string (will be used if message value is null)
     *  @param isShowSnackbarMessage {Boolean} A boolean to switch between snackBar and toast
     */
    fun showMessage(resId: Int? = null, message: String? = null, isShowSnackbarMessage: Boolean = false) {
        if (isShowSnackbarMessage) {
            mMyCustomLoader.showSnackBar(view, message ?: getString(resId!!))
        } else {
            mMyCustomLoader.showToast(message ?: getString(resId!!))
        }
    }

    /**
     *  @description call this method to show progress dialog
     *  @param isShowProgress {Boolean} Boolean to show or hide progress bar
     */
    fun showProgressDialog(isShowProgress: Boolean) {
        if (isShowProgress) {
            mMyCustomLoader.showProgressDialog()
        } else {
            mMyCustomLoader.dismissProgressDialog()
        }
    }

    /**
     *  @description call this method to hide progress dialog
     */
    fun dismissDialogFragment() {
        (activity!!.supportFragmentManager.findFragmentByTag(getString(R.string.dialog)) as androidx.fragment.app.DialogFragment).dismiss()
    }

    /**
     *  @description call this method to start home activity and finish current activity
     */
    protected fun navigateToMainActivity() {
        startActivity(Intent(activityContext, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        activity?.finish()
    }

    /**
     *  @description observe live data values
     */
    private fun observeBaseProperties() {
        //observe session id
        viewModel?.isSessionExpired()?.observe(viewLifecycleOwner, Observer {
            AsyncTask.execute {
                Firebase.messaging.deleteToken()
            }
            ApplicationGlobal.showOnBoardings = false
            if (it!!) navigateToMainActivity()
        })
        // Observer Errors
        viewModel?.getErrorHandler()?.observe(viewLifecycleOwner, Observer {
            showMessage(resId = it?.getErrorResource())
        })

        // Observe Loader
        viewModel?.isShowLoader()?.observe(viewLifecycleOwner, Observer {
            showProgressDialog(it!!)
        })

        // Observe Retrofit Errors
        viewModel?.getMessage()?.observe(viewLifecycleOwner, Observer {
            showMessage(it?.resId, it?.message)
        })
    }

    /**
     * @description Helper method to change Fragments making use of doFragmentTransaction method of Activity.
     * @param frag is the Fragment to which you want to perform transaction to.
     * @param transactionTag is the TAG of the Fragment you're performing transaction to.
     * @param isAddFragment is Boolean to represent if fragment is being added or replaced. True, by default.
     * @param showAnimation represents whether to show animations while performing Fragment Transaction.
     */
    fun performFragTransaction(
            frag: Fragment, transactionTag: String,
            isAddFragment: Boolean = true,
            enterAnim: Int = 0, exitAnim: Int = 0, popEnterAnim: Int = 0, popExitAnim: Int = 0,
    ) {
        val act = activity as BaseAppCompactActivity
        act.doFragmentTransaction(act.supportFragmentManager, R.id.flFragContainer, frag,
                isAddFragment = isAddFragment, tag = transactionTag,
                enterAnimation = enterAnim, exitAnimation = exitAnim,
                popEnterAnimation = popEnterAnim,
                popExitAnimation = popExitAnim)
    }

    /**
     * @description Override onAttach to cast context as DrawerCallbacks for drawer layout
     * interface to access drawer functions of activity.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerCallbacks = context as DrawerCallbacks
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract val layoutId: Int

    abstract val viewModel: BaseViewModel?

//    abstract val isNavigationBarEnabled: Boolean
//
//    abstract val isMakeStatusBarTransparent: Boolean

    abstract fun init()

}