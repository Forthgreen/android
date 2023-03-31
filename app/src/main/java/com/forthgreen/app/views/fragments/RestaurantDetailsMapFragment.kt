package com.forthgreen.app.views.fragments

import android.annotation.SuppressLint
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Restaurant
import com.forthgreen.app.viewmodels.BaseViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_restaurant_details_map.*
import kotlinx.android.synthetic.main.toolbar.*


/**
 * @author shraychona@gmail.com
 * @since 27 Oct 2020
 */
class RestaurantDetailsMapFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "RestaurantDetailsMapFrag"
        private const val BUNDLE_EXTRAS_DETAILS = "restaurantDetails"

        fun newInstance(restaurant: Restaurant): RestaurantDetailsMapFragment {
            return RestaurantDetailsMapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRAS_DETAILS, restaurant)
                }
            }
        }
    }

    private val mDetails by lazy { arguments?.getParcelable<Restaurant>(BUNDLE_EXTRAS_DETAILS)!! }

    /**
     * Location Variables
     */
    // To keep track of Location and Coordinates and assign it when received from Places Autocomplete.
    private val mFusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    //Location Objects
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback

    /**
     * Map variables
     */
    private var googleMap: GoogleMap? = null
    private val ZOOM_LEVEL = 13f
    private val markerMapping = mutableMapOf<String, String>()
    private var selectedMarker: Marker? = null


    override val layoutId: Int
        get() = R.layout.fragment_restaurant_details_map

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        //setup toolbar
        tvToolbarTitle.text = mDetails.name

        // setup MAP
        try {
            mapDetail.onCreate(null)
            mapDetail.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fetchCurrentLocation()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        this.googleMap = googleMap
        addMarkers(mDetails)
    }

    private fun addMarkers(restaurant: Restaurant) {
        googleMap?.let { map ->
            map.clear()
            markerMapping.clear()
            val markerOpt = MarkerOptions().apply {
                position(LatLng(restaurant.location.coordinates[0],
                        restaurant.location.coordinates[1]))
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_map_selected))
            }
            val marker = map.addMarker(markerOpt)
            selectedMarker = marker
            markerMapping[marker.id] = restaurant._id
        }
    }

    /**
     * Animate map to current location
     */
    fun animateMap() {
        if (mLatitude != null && mLongitude != null) {

            val markerOpt = MarkerOptions().apply {
                position(LatLng(mLatitude!!, mLongitude!!))
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_current_location))
            }

            val builder = LatLngBounds.Builder()
            selectedMarker?.let { marker ->
                builder.include(marker.position)
            }
            builder.include(markerOpt.position)
            val bounds = builder.build()
            val padding = resources.getDimension(R.dimen.map_detail_padding).toInt() // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap?.let {
                it.addMarker(markerOpt)
                it.animateCamera(cu)
            }
        }
    }

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
                //Update Map Location according to coordinates.
                animateMap()
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
                            //Update Map Location according to coordinates.
                            animateMap()
                        } else if (canRetry) {
                            getCurrentLocation(false)
                            Log.d("locationStatus", "location fetched failed returned empty")
                        }
                    }.addOnFailureListener {
                        Log.d(BaseLocationRecyclerViewFragment.TAG, "getCurrentLocation: $it")
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
                    exception.startResolutionForResult(activity, BaseLocationRecyclerViewFragment.REQUEST_LOCATION)
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

                        //Update Map Location according to coordinates.
                        animateMap()

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

    /**
     *  Map LifeCycle Methods
     */
    override fun onResume() {
        super.onResume()
        mapDetail?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapDetail?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapDetail?.onStop()
    }

    override fun onPause() {
        mapDetail?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mapDetail?.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapDetail?.onLowMemory()
    }
}