package com.forthgreen.app.views.interfaces

/**
 * Created on 16-04-2021
 * @description Interface to implement callback methods for drawer in activity.
 */
interface DrawerCallbacks {

    fun openNavDrawer()
    fun closeNavDrawer()
    fun lockNavDrawer()
    fun unlockNavDrawer()
}