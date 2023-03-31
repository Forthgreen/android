package com.forthgreen.app.views.activities

import android.content.Context.POWER_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.forthgreen.app.R

/**
 * Created by ShrayChona on 03-10-2018.
 * @description extend this class for basic activity
 */
abstract class BaseAppCompactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        val window = window
        if (isMakeStatusBarTransparent) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorTransparent)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            window.statusBarColor = ContextCompat.getColor(this,
                    R.color.colorPrimaryDark)
        }
        init()
    }

    /**
     *  @description handles action when physical back button is pressed
     */
    override fun onBackPressed() {
        if (supportFragmentManager != null && supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else if (supportFragmentManager.fragments.isNotEmpty() && supportFragmentManager.fragments[supportFragmentManager.fragments.size - 1].tag == "com.bumptech.glide.manager" && supportFragmentManager.fragments[0].tag == "com.bumptech.glide.manager") {
            finish()
        } else {
            finish()
        }
    }

    abstract val layoutId: Int

    abstract val isMakeStatusBarTransparent: Boolean

    abstract fun init()
}

/**
 *  @description Generic layout inflater
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

/**
 *  @description Generic function do fragment transaction
 *  @param fragManager {FragmentManager}
 *  @param containerViewId {Int} id of view for populating fragment
 *  @param fragment {Fragment} fragment to be populated
 *  @param tag {String} add tag for managing fragments in fragmentBackStack
 *  @param enterAnimation {Int} Enter animation for fragment
 *  @param exitAnimation {Int} Enter animation for fragment
 *  @param popEnterAnimation {Int} Enter animation for fragment
 *  @param popExitAnimation {Int} Enter animation for fragment
 *  @param isAddFragment {Boolean} Boolean to switch between add or replace fragment
 *  @param isAddToBackStack {Boolean} Add fragment to fragmentBackStack or not
 *  @param allowStateLoss {Boolean} make true if fragment has any pending transactions with delays
 */
fun AppCompatActivity.doFragmentTransaction(
        fragManager: androidx.fragment.app.FragmentManager = supportFragmentManager,
        @IdRes containerViewId: Int,
        fragment: androidx.fragment.app.Fragment,
        tag: String = "",
        @AnimatorRes enterAnimation: Int = 0,
        @AnimatorRes exitAnimation: Int = 0,
        @AnimatorRes popEnterAnimation: Int = 0,
        @AnimatorRes popExitAnimation: Int = 0,
        isAddFragment: Boolean = true,
        isAddToBackStack: Boolean = true,
        allowStateLoss: Boolean = false) {

    // turn of animations if power saver is on
    val powerManager = getSystemService(POWER_SERVICE) as PowerManager
    val isPowerSaverEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode

    val fragmentTransaction = fragManager.beginTransaction()
    if (!isPowerSaverEnabled)
        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)

    if (isAddFragment) {
        fragmentTransaction.add(containerViewId, fragment, tag)
    } else {
        fragmentTransaction.replace(containerViewId, fragment, tag)
    }

    if (isAddToBackStack) {
        fragmentTransaction.addToBackStack(null)
    }

    if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    } else {
        fragmentTransaction.commit()
    }
}