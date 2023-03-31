package com.forthgreen.app.repository.models

/**
 * Created by ShrayChona on 03-10-2018.
 */
data class Tab(val tabFragment: androidx.fragment.app.Fragment, val tabName: String = "", val tabIcon: Int?,
               val isShowTabName: Boolean)