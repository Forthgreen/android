package com.forthgreen.app.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.forthgreen.app.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.*

/**
 * Created by Chetan Tuteja on 19-Oct-20.
 * @description extend this class for fragment setup with location and recycler view.
 */
abstract class BaseLocationRecyclerViewFragment : BaseRecyclerViewFragment() {

    companion object {
        const val TAG = "BaseLocationFragment"
        const val REQUEST_LOCATION: Int = 321
    }

    //Variables

    private val mFusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    //Location Objects
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback

    /**
     * @description This method fetches the current location of the user with the help of the GPS.
     */
    fun fetchCurrentLocation() {
        //If permission is already granted, proceed to fetch location else ask for permissions.
        if (isAllGranted(Permission.ACCESS_FINE_LOCATION)) {
            fetchCoordinates()
        } else {
            askForPermissions(Permission.ACCESS_FINE_LOCATION) { result ->
                if (result.isAllGranted(Permission.ACCESS_FINE_LOCATION)) {
                    fetchCoordinates()
                } else {
                    showMessage(R.string.location_permission_needed)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCoordinates() {
        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                mLongitude = location.longitude
                mLatitude = location.latitude
                reverseGeocode()
                Log.d("locationStatus", "location fetched from fused client lat - ${location.latitude} long - ${location.longitude}")
            } else {
                getCurrentLocation(true)
            }
        }
    }


    /**
     * @description This method fetches the current location of user, firstly by seeing if the GPS is enabled and all settings required
     * are satisfied or not, else it asks for those settings and then fetches the location based on that.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(canRetry: Boolean) {
        /**
         * @param interval This method sets the rate in milliseconds at which your app prefers to receive location updates
         * @param fastestInterval This method sets the fastest rate in milliseconds at which your app can handle location updates
         * @param priority This method sets the priority of the request, which gives the Google Play services location
         *                  services a strong hint about which location sources to use.
         */
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val mBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setNeedBle(true)
                .setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(requireContext()).checkLocationSettings(mBuilder.build())

        //See if the location settings are satisfied else show dialog for granting location.
        result.addOnSuccessListener {
            try {
                Log.d("locationStatus", "before handler")
                Handler().postDelayed({
                    Log.d("locationStatus", "after handler")
                    mFusedLocationClient.lastLocation.addOnSuccessListener { location ->

                        if (location != null) {
                            Log.d("locationStatus", "location fetched lat - ${location.latitude} long - ${location.longitude}")
                            mLatitude = location.latitude
                            mLongitude = location.longitude
                            reverseGeocode()
                        } else if (canRetry) {
                            getCurrentLocation(false)
                            Log.d("locationStatus", "location fetched failed returned empty")
                        }
                    }.addOnFailureListener {
                        Log.d(TAG, "getCurrentLocation: $it")
                    }
                }, 1500)
            } catch (e: java.lang.Exception) {
                Log.d("locationStatus", "after crash msg - ${e.message}")
            }
        }


        result.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult().
                    Log.d("locationStatus", "after handler need to turn on location")
                    exception.startResolutionForResult(activity, REQUEST_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.d("locationStatus", "after crash msg - ${sendEx.message}")
                }
            }
        }

        //Location Callback to fetch location updates.
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                if (result != null) {
                    if (result.locations.isNotEmpty()) {
                        mLongitude = result.lastLocation.longitude
                        mLatitude = result.lastLocation.latitude

                        reverseGeocode()

                        //Remove callbacks once Location is fetched successfully.
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                    }
                }
                super.onLocationResult(result)
            }
        }

        //If Location is still null, start callbacks for regular Location Updates.
        if (mLatitude == null || mLongitude == null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
        }
    }

    //Find the address via Location coordinates
    private fun reverseGeocode() {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            if (mLatitude != null && mLongitude != null) {
                val addresses = geocoder.getFromLocation(mLatitude!!, mLongitude!!, 1)

                //Get address via Location
                if (addresses.isNotEmpty() && addresses.first().countryName != null && addresses.first().locality != null) {
                    val textAddress = "${addresses.first().locality}, ${addresses.first().countryName}"
                    onLocationFetchSuccess(mLatitude!!, mLongitude!!, textAddress)
                } else {
                    Log.d("locationStatus", "reverseGeocode failed")
                    onLocationFetchSuccess(mLatitude!!, mLongitude!!, "")
                }
            }
        } catch (e: Exception) {
            Log.d("locationStatus", "after crash reverseGeocode msg - ${e.message}")
            e.printStackTrace()

            //If reverse geocode fails, send empty location address.
            if (mLatitude != null && mLongitude != null) {
                onLocationFetchSuccess(mLatitude!!, mLongitude!!, "")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_LOCATION) {
            //If location is turned on via dialog, get current location coordinates.
            getCurrentLocation(true)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Abstract method to provide Location update
    abstract fun onLocationFetchSuccess(mLatitude: Double, mLongitude: Double, mAddress: String)
}