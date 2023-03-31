package com.forthgreen.app.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ViewPagerAdapterCommon(mManager: FragmentManager) : FragmentStatePagerAdapter(mManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList: ArrayList<Fragment> by lazy {
        ArrayList<Fragment>()
    }


    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFrag(frag: Fragment) {
        mFragmentList.add(frag)
    }
}