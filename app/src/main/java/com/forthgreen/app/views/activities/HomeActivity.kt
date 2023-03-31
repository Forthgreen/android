package com.forthgreen.app.views.activities

import android.util.Log
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.forthgreen.app.R
import com.forthgreen.app.views.adapters.ViewPagerAdapterCommon
import com.forthgreen.app.views.fragments.MyBrandsFragment
import com.forthgreen.app.views.fragments.SettingsFragment
import com.forthgreen.app.views.fragments.ShopCategoriesFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseAppCompactActivity() {

    companion object {
        const val TAG = "HomeActivity"
    }

    //Variables
    private var prevMenuItem: MenuItem? = null        //To keep track of previous menu item selected.

    override val layoutId: Int
        get() = R.layout.activity_home

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun init() {
        //Sets default icon tint to null for BottomNavBar to use custom tints
        navbarHome.itemIconTintList = null
        setupViewpager()
        setupListeners()
    }

    private fun setupViewpager() {
        //Add fragments via the adapter
        try {
            val mAdapter = ViewPagerAdapterCommon(supportFragmentManager)
            mAdapter.addFrag(ShopCategoriesFragment())
            mAdapter.addFrag(MyBrandsFragment())
            mAdapter.addFrag(SettingsFragment())
            vpBottomNav.adapter = mAdapter
            vpBottomNav.offscreenPageLimit = 5
        } catch (e: Exception) {
            Log.d(TAG, "setupViewPager: Error")
        }
    }

    private fun setupListeners() {
        //Set Bottom navigation bar to work with ViewPager
        navbarHome.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    vpBottomNav.currentItem = 0
                    true
                }
                R.id.nav_shop_page -> {
                    vpBottomNav.currentItem = 1
                    true
                }
                R.id.nav_restaurant -> {
                    vpBottomNav.currentItem = 2
                    true
                }
            }
            false
        }

        //Set ViewPager changes to reflect upon BottomNavBar
        vpBottomNav.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    navbarHome.menu.getItem(0).isChecked = false
                }

                navbarHome.menu.getItem(position).isChecked = true
                prevMenuItem = navbarHome.menu.getItem(position)
            }
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager != null && supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else if (supportFragmentManager.fragments.isNotEmpty() && supportFragmentManager.fragments[supportFragmentManager.fragments.size - 1].tag == "com.bumptech.glide.manager" && supportFragmentManager.fragments[0].tag == "com.bumptech.glide.manager") {
            finish()
        } else {
            finish()
        }
    }
}