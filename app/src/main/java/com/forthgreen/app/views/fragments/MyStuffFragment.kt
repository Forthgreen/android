package com.forthgreen.app.views.fragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Tab
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.gone
import kotlinx.android.synthetic.main.fragment_my_stuff.*
import kotlinx.android.synthetic.main.toolbar.*

class MyStuffFragment : BaseTabLayoutFragment() {

    companion object {
        const val TAG = "MyStuffFragment"
        const val LOCAL_INTENT_SHOW_SHIMMER = "LOCAL_INTENT_SHOW_SHIMMER"
    }

    override val layoutId: Int
        get() = R.layout.fragment_my_stuff

    override val viewModel: BaseViewModel?
        get() = null

    override fun initTabs() {
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        val listOfTabs = ArrayList<Tab>()

        listOfTabs.add(Tab(MyProductsFragment(), getString(R.string.products_tab_title), null, true))
        listOfTabs.add(Tab(MyBrandsFragment(), getString(R.string.brands_tab_title), null, true))
        listOfTabs.add(Tab(FollowedRestaurantsFragment(), getString(R.string.restaurant_tab_title), null, true))

        setViewPager(getString(R.string.my_stuff_toolbar_title), listOfTabs)

        //set toolbar navigation to null
        toolbar.navigationIcon = null
    }

    private fun setupListeners() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_SHOW_SHIMMER))
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    fun showShimmer() {
        viewPager.currentItem = 0
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent(LOCAL_INTENT_SHOW_SHIMMER))
    }

    override fun onResume() {
        super.onResume()
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            tabLayout.gone()
            viewPager.gone()
            callUserLoginDialog()
        }
    }

    private fun callUserLoginDialog() {
        val userLoginDialog = UserLoginDialog()
        userLoginDialog.showUserLoginDialog(requireActivity() as AppCompatActivity, object :
            LoginButtonClickInterface {
            override fun loginButtonClick() {
                performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            }
        })
    }

}