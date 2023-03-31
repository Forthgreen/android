package com.forthgreen.app.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.forthgreen.app.views.fragments.ImageFragment

/**
 * @author shraychona@gmail.com
 * @since 03 Jun 2020
 */
class ImagePreviewAdapter(fragmentManager: FragmentManager, private val imagesList: ArrayList<String>) :
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
            ImageFragment.newInstance(imagesList[position])

    override fun getCount(): Int = imagesList.size
}