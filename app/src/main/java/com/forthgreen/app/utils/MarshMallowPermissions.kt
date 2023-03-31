package com.forthgreen.app.utils

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import com.forthgreen.app.R

/**
 * Created by ShrayChona on 03-10-2018.
 */
class MarshMallowPermissions(private val mFragment: androidx.fragment.app.Fragment) {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 991
        const val READ_CONTACTS_PERMISSION_REQUEST_CODE = 4
    }

    private var mActivity: Activity = mFragment.activity!!

    fun requestPermissionForLocation() {
        if (mFragment.shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            showAlertDialog(mActivity.getString(R.string.location_permission_needed),
                    DialogInterface.OnClickListener { _, _ ->
                        mFragment.requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                LOCATION_PERMISSION_REQUEST_CODE)
                    }, null)
        } else {
            mFragment.requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    val isPermissionGrantedForContacts: Boolean
        get() = ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    fun requestPermissionForContacts() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.READ_CONTACTS)) {
            showAlertDialog(mActivity.getString(R.string.contacts_permission_needed),
                    DialogInterface.OnClickListener { _, _ ->
                        mFragment.requestPermissions(
                                arrayOf(Manifest.permission.READ_CONTACTS),
                                READ_CONTACTS_PERMISSION_REQUEST_CODE)
                    }, null)
        } else {
            mFragment.requestPermissions(
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_REQUEST_CODE)
        }
    }


    private fun showAlertDialog(message: String,
                                okListener: DialogInterface.OnClickListener,
                                cancelListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton(mActivity.getString(R.string.ok), okListener)
                .setNegativeButton(mActivity.getString(R.string.cancel), cancelListener)
                .create()
                .show()
    }

}