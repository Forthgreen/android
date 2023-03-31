package com.forthgreen.app.views.fragments

import com.forthgreen.app.repository.models.Tab
import com.forthgreen.app.views.adapters.TabsAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_base_tab_layout.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by ShrayChona on 04-10-2018.
 * @description extend this class for fragment setup with viewpager
 */
abstract class BaseTabLayoutFragment : BaseFragment() {

    override fun init() {
        // uncomment if fragment has toolbar
//        if (null != toolbar) {
//            // remove toolbar background and elevation
//            if (GeneralFunctions.isAboveLollipopDevice) {
//                toolbar.elevation = 0f
//            }
//            toolbar.setBackgroundColor(Color.TRANSPARENT)
//        }
        initTabs()
    }

    /**
     *  @description call this method to setup viewpager
     *  @param toolbarTitle {String} toolbar title text
     *  @param tabsList {List<Tab>} list of tabs to be shown in viewpager
     */
    fun setViewPager(toolbarTitle: String, tabsList: List<Tab>) {
        if (null != tvToolbarTitle) {
            tvToolbarTitle.text = toolbarTitle
        }
        val tabsAdapter = TabsAdapter(childFragmentManager, tabsList)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = tabsAdapter
        if (null != tabLayout) {
            if (6 <= tabsList.size) {
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
            tabLayout.setupWithViewPager(viewPager)

            if(tabsList.first().tabIcon != null){
                //Set Tab Icons
                tabsList.forEachIndexed { index, tab ->
                    tabLayout.getTabAt(index)?.setIcon(tab.tabIcon!!)
                }
            }
//            changeTabsFont(tabLayout)
//        disableTabClick()
        }
    }


    fun setViewPagerWithOutToolbarTitle(tabsList: List<Tab>) {
        val tabsAdapter = TabsAdapter(childFragmentManager, tabsList)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = tabsAdapter
        if (null != tabLayout) {
            if (6 <= tabsList.size) {
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
            tabLayout.setupWithViewPager(viewPager)

            if(tabsList.first().tabIcon != null){
                //Set Tab Icons
                tabsList.forEachIndexed { index, tab ->
                    tabLayout.getTabAt(index)?.setIcon(tab.tabIcon!!)
                }
            }
//            changeTabsFont(tabLayout)
//        disableTabClick()
        }
    }

    /**
     *  @description call this method if you want to disable tabs change on click
     */
//    private fun disableTabClick() {
//        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
//        for (i in 0 until tabStrip.childCount) {
//            tabStrip.getChildAt(i).setOnTouchListener { v, event -> true }
//        }
//    }

    /**
     *  @description call this method to change tab title font
     *  @param tabLayout {Tablayout}
     */
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private fun changeTabsFont(tabLayout: com.google.android.material.tabs.TabLayout) {
//        val vg = tabLayout.getChildAt(0) as ViewGroup
//        val tabsCount = vg.childCount
//        for (j in 0 until tabsCount) {
//            val vgTab = vg.getChildAt(j) as ViewGroup
//            val tabChildsCount = vgTab.childCount
//            for (i in 0 until tabChildsCount) {
//                val tabViewChild = vgTab.getChildAt(i)
//                if (tabViewChild is TextView) {
//                    tabViewChild.textSize=
//                    tabViewChild.typeface = Typeface.createFromAsset(activity.getAssets(),
//                            activity.getString(R.string.font_proximanovasemibold))
//                }
//            }
//        }
//    }

    abstract fun initTabs()

}