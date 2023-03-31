package com.forthgreen.app.views.adapters

import com.forthgreen.app.repository.models.Tab

/**
 * Created by ShrayChona on 03-10-2018.
 */
class TabsAdapter(private val fm: androidx.fragment.app.FragmentManager, private val tabsList: List<Tab>) :
        androidx.fragment.app.FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment = tabsList[position].tabFragment

    override fun getCount(): Int = tabsList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return if (tabsList[position].isShowTabName) tabsList[position].tabName else ""
    }
}