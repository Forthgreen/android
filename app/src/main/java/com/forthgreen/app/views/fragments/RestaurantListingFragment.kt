package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.repository.models.Restaurant
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.RestaurantListingViewModel
import com.forthgreen.app.views.adapters.RestaurantListingAdapter
import com.forthgreen.app.views.adapters.SingleRestaurantAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.setOnSafeClickListener
import com.forthgreen.app.views.utils.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_restaurants_listing.*
import kotlinx.android.synthetic.main.fragment_restaurants_listing.flShimmer
import kotlinx.android.synthetic.main.fragment_restaurants_listing.toolbar
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

/**
 * @author shraychona@gmail.com
 * @since 13 Oct 2020
 */
class RestaurantListingFragment : BaseLocationRecyclerViewFragment(),
        LoadMoreListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        RestaurantListingAdapter.RestaurantListingCallback, GoogleMap.OnCameraMoveStartedListener,
        SingleRestaurantAdapter.SingleRestaurantCallback {

    companion object {
        const val TAG = "RestaurantListingFrag"
        private const val mResultSize = 30
        private const val PRESENT_LOCATION_MARKER_TAG = "PresentLocationMarket"
    }

    //Variables
    private var mPage = 1       // To keep track of listing results.
    private var mMapPage = 1    // To keep track of map results.
    private var filterList = Constants.restaurantFilterList
    private val restaurantListMap = mutableListOf<Restaurant>()

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    /**
     * Location Variables
     */
    // To keep track of Location and Coordinates and assign it when received from Places Autocomplete.
    private var mLocation: String? = null
    private var mCurrentCoordinates: Array<Double>? = null
    private var markerCoordinates = mutableMapOf<Double, Double>()
    private var mMapCoordinates: Array<Double>? = null

    /**
     * Map variables
     */
    private var googleMap: GoogleMap? = null
    private val ZOOM_LEVEL = 13f
    private var isMapVisible = false
    private val markerMapping = mutableMapOf<String, String>()
    private var selectedMarker: Marker? = null

    private val restaurantListingAdapter by lazy {
        RestaurantListingAdapter(this, this)
    }

    private val singleRestaurantAdapter by lazy {
        SingleRestaurantAdapter(this)
    }

    private val mRestaurantListingViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(RestaurantListingViewModel::class.java)
    }

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    override val layoutId: Int
        get() = R.layout.fragment_restaurants_listing

    override val viewModel: BaseViewModel?
        get() = mRestaurantListingViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = restaurantListingAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = mLinearLayoutManager

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setUpViews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpViews() {
        //Set toolbar title as drawable
        tvToolbarTitle.text = getString(R.string.restaurant_toolbar_title)
      //  toolbar.navigationIcon = null
        toolbar.toolbar.navigationIcon = null

        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_map1)
            visible()
        }

      //  tvMap.text = getString(R.string.map_restaurant_label)

        //Set Linear Layout Manager
        mLinearLayoutManager = LinearLayoutManager(requireContext())


        // Show Shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        groupRestaurantListView.gone()

        // setup MAP
        try {
            mapRestaurants.onCreate(null)
            mapRestaurants.getMapAsync(this)
            hideMap()
            hideMapViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Set the single Restaurant Recycler View
        rvSingleRestaurant.apply {
            adapter = singleRestaurantAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Setup Push down anim
        PushDownAnim.setPushDownAnimTo(tvSearchHere, tvShowMore)

        // Register Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter(HomeFilterFragment.LOCAL_INTENT_RESTAURANT_FILTER_UPDATED))

        fetchCurrentLocation()
    }

    private fun setUpListeners() {
//        toolbar.setNavigationOnClickListener {
//            //show_preferences
//            performFragTransaction(HomeFilterFragment.newInstance(filterList, true), HomeFilterFragment.TAG)
//        }

        ivToolbarActionEnd.setOnSafeClickListener {
            //Hide-Show Map according to current visibility.
            if (isMapVisible) {
              //  tvMap.text = getString(R.string.map_restaurant_label)
                ivToolbarActionEnd.apply {
                    setImageResource(R.drawable.ic_map1)
                    visible()
                }
                hideMapViews()
                hideMap()
                hideRestaurantCard()
            } else {

                ivToolbarActionEnd.apply {
                    setImageResource(R.drawable.ic_map_list)
                    visible()
                }
               // tvMap.text = getString(R.string.list_restaurant_label)
                showMap()
            }
        }

        tvSearchHere.setOnClickListener {
            if (isMapVisible) {
                performMapAPICall()
            }
        }
        tvShowMore.setOnClickListener {
            if (isMapVisible) {
                mMapPage++
                performMapAPICall()
            }
        }
        ivCurrentLocation.setOnClickListener {
            if (isMapVisible) {
                if (!mCurrentCoordinates.isNullOrEmpty()) {
                    animateMap(mCurrentCoordinates!!.toList())
                    addCurrentLocationMarker(mCurrentCoordinates!!.toList())
                }
            }
            ivCurrentLocation.gone()
        }
    }

    //Helper method to observe data via ViewModel
    private fun observeProperties() {
        mRestaurantListingViewModel.onRestaurantsFetched().observe(viewLifecycleOwner, Observer { restaurantListing ->
            //Hide Shimmer
            flShimmer.stopShimmer()
            flShimmer.gone()
            groupRestaurantListView.visible()
            hideMapViews()

            if (mPage == 1 && restaurantListing.data.isEmpty()) {
                restaurantListingAdapter.submitList(emptyList(), mPage, false)
                //showNoDataText(message = "No Restaurants found.", resId = null)
                setupNoDataText()
            } else {
                hideNoDataText()
                restaurantListingAdapter.submitList(
                        restaurantListing.data,
                        mPage,
                        restaurantListing.hasMore
                )
            }
        })
        mRestaurantListingViewModel.onReceivedDetails().observe(viewLifecycleOwner, { details ->
            singleRestaurantAdapter.submitList(listOf(details.data), 1, false)
        })

        mRestaurantListingViewModel.onRestaurantsForMapFetched().observe(viewLifecycleOwner, { restaurantListing ->
            if (mMapPage == 1 && restaurantListing.data.isEmpty()) {
                // Clear Previous Markings and Animate to current Location
                googleMap?.clear()
                selectedMarker = null
                if (!mCurrentCoordinates.isNullOrEmpty()) {
//                    animateMap(mMapCoordinates!!.toList())
                    addCurrentLocationMarker(mCurrentCoordinates!!.toList())
                }
                hideMapViews()
            } else {
                // If Page is 1, clear all data and store the data in local list.
                if (mMapPage == 1) {
                    restaurantListMap.clear()
                }
                restaurantListMap.addAll(restaurantListing.data)
                addMarkers(mMapPage == 1, restaurantListing.data)
                // Check if there is there are more restaurants data for this location.
                if (isMapVisible && restaurantListing.hasMore) {
                    tvShowMore.visible()
                    tvSearchHere.gone()
                } else {
                    tvShowMore.gone()
                    tvSearchHere.gone()
                }
            }
        })
    }

    override fun onPullDownToRefresh() {
        if (!mCurrentCoordinates.isNullOrEmpty()) {
            mPage = 1
            // Show Shimmer
//            flShimmer.startShimmer()
//            flShimmer.visible()
//            groupRestaurantListView.gone()
            hideNoDataText()
            performListAPICall(false)
        }
    }

    override fun onLoadMore() {
        if (!mCurrentCoordinates.isNullOrEmpty()) {
            mPage++
            performListAPICall(false)
        }
    }

    //For Restaurant Click
    override fun onRestaurantClick(restaurant: Restaurant) {
        performFragTransaction(RestaurantDetailsFragment.newInstance(restaurant), RestaurantDetailsFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
    }

    // For Single Restaurant Click
    override fun onSingleRestaurantClick(restaurant: Restaurant, dismissCard: Boolean) {
        if (dismissCard) {
            hideRestaurantCard()
        } else {
            performFragTransaction(RestaurantDetailsFragment.newInstance(restaurant),
                    RestaurantDetailsFragment.TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right)
        }
    }

    override fun onSearchTextChange(query: String) {
        // Search restaurant on the basis of entered text via viewModel
        mRestaurantListingViewModel.fetchRestaurants(false, mPage, mResultSize,
                mCurrentCoordinates!![0], mCurrentCoordinates!![1], getSelectedFilterIds(), query)
    }

    /**
     *  MAP Methods
     */
    private fun showMap() {
        mapRestaurants.visible()
        isMapVisible = true
    }

    private fun hideMap() {
        mapRestaurants.gone()
        isMapVisible = false
    }

    private fun hideMapViews() {
        tvSearchHere.gone()
        tvShowMore.gone()
        ivCurrentLocation.gone()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        this.googleMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnCameraMoveStartedListener(this)
    }

    private fun animateMap(coordinates: List<Double>) {
        val latLong = if (coordinates.size > 1) LatLng(coordinates.first(), coordinates[1]) else LatLng(0.0, 0.0)
        googleMap?.let {
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, ZOOM_LEVEL))
        }
    }

    private fun addMarkers(clearPrevious: Boolean, markerList: List<Restaurant>) {
        googleMap?.let { map ->
            if (clearPrevious) {
                map.clear()
                markerMapping.clear()
                selectedMarker = null

                // Add Current Location Marker again
                if (!mCurrentCoordinates.isNullOrEmpty()) {
                    addCurrentLocationMarker(mCurrentCoordinates!!.toList())
                }
            }
            markerList.forEach { restaurant ->
                val markerOpt = MarkerOptions().apply {
                    position(LatLng(restaurant.location.coordinates[0],
                            restaurant.location.coordinates[1]))
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_map_regular))
                }
                val marker = map.addMarker(markerOpt)
                markerMapping[marker.id] = restaurant._id
            }
        }
    }

    //Helper function to hide Restaurant card and select marker icon.
    private fun hideRestaurantCard() {
        rvSingleRestaurant.gone()
        selectedMarker?.setIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_map_regular)
        )
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.let { currentMarker ->

            // If Current Marker is Present Location Marker, just return.
            if (currentMarker.tag != null &&
                    (currentMarker.tag as String) == PRESENT_LOCATION_MARKER_TAG) {
                return true
            }

            //Reset Icon for old selected Marker if any
            selectedMarker?.setIcon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_map_regular)
            )

            //Update Icon on Selection
            currentMarker.setIcon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_map_selected)
            )

            //Save Current Marker as Selected
            selectedMarker = currentMarker

            /*
             * Fetch the corresponding RestaurantId from Marker
             * Filter our RestaurantList for Restaurant that matches the Id and show it.
             */
            markerMapping[currentMarker.id]?.let { restaurantId ->
                rvSingleRestaurant.visible()
                singleRestaurantAdapter.submitList(
                        restaurantListMap.filter { restaurant -> restaurant._id == restaurantId },
                        1,
                        false
                )
                mRestaurantListingViewModel.getRestaurantDetails(false, restaurantId)
            }
        }
        return true
    }

    /**
     * Location picker method
     */
    override fun onLocationFetchSuccess(mLatitude: Double, mLongitude: Double, mAddress: String) {
        // Save the user current coordinates
        mRestaurantListingViewModel.saveUserLocation(mLatitude, mLongitude)

        // Store the fetched coordinates
        mCurrentCoordinates = arrayOf(mLatitude, mLongitude)
        mLocation = mAddress

        // Update Map Location according to coordinates.
        animateMap(mCurrentCoordinates!!.toList())

        // Add Current Location Icon
        addCurrentLocationMarker(mCurrentCoordinates!!.toList())

        // Initially Update the map coordinates to current location coordinates.
        mMapCoordinates = mCurrentCoordinates

        // Show Shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        groupRestaurantListView.gone()

        // Fetch Results via API
        performListAPICall(true)

        // Fetch Results for map via API
        performMapAPICall()
    }

    private fun getSelectedFilterIds(): Array<Int> {
        return filterList.filter { restaurantFilter ->
            restaurantFilter.isSelected
        }.map { restaurantFilter ->
            restaurantFilter.id
        }.toTypedArray()
    }

    //Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                if (intent.hasExtra(HomeFilterFragment.BUNDLE_EXTRAS_FILTER_LIST)) {
                    val updatedFilterList =
                            intent.getParcelableArrayListExtra<Filter>(HomeFilterFragment.BUNDLE_EXTRAS_FILTER_LIST)!!
                    filterList = updatedFilterList
                    mPage = 1
                    //Show Shimmer
//                    flShimmer.startShimmer()
//                    flShimmer.visible()
//                    groupRestaurantListView.gone()
                    if (!mCurrentCoordinates.isNullOrEmpty()) {
                        performListAPICall(true)
                    }
                }
            }
        }
    }

    //Helper function to Scroll to top
    fun scrollToTop() {
        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        mLinearLayoutManager.startSmoothScroll(smoothScroller)
    }

    fun showShimmer() {
        isMapVisible = false
        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_map1)
            visible()
        }
        // Show Shimmer
        flShimmer.startShimmer()
        flShimmer.visible()
        groupRestaurantListView.gone()
        hideMap()
        hideMapViews()

        // Fetch Results via API
        performListAPICall(false)
    }

    // Helper method to add marker for Current Location.
    private fun addCurrentLocationMarker(coordinates: List<Double>) {
        googleMap?.let { map ->
            val markerOpt = MarkerOptions().apply {
                position(LatLng(coordinates[0],
                        coordinates[1]))
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_current_location))
            }
            map.addMarker(markerOpt).apply {
                tag = PRESENT_LOCATION_MARKER_TAG
            }
        }
    }

    // Helper method to show custom spannable No Data Text.
    private fun setupNoDataText() {
        // Make No Data Text Visible and underline the spannable text and make it clickable.
        tvNoData.apply {
            visible()
            text = getString(R.string.no_restaurant_text)
        }

        makeTextLink(tvNoData, str = getString(R.string.let_us_know_spannable), bold = false,
                underlined = true, color = null, action = {
            // Open Web Browser on Click.
            val webPage: Uri = Uri.parse(getString(R.string.no_restaurant_hyper_link))
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                showMessage(R.string.website_not_available_error)
            }
        })
    }

    /**
     * @description Helper method to make text view Spannable.
     *
     *
     * @param str {String} part of string on which to perform change.
     * @param underlined {Boolean} whether the text should be underlined.
     * @param bold {Boolean} whether the text should be bold.
     * @param action {Lambda} Action to be performed onClick.
     */
    private fun makeTextLink(
            textView: TextView, str: String, underlined: Boolean, bold: Boolean,
            color: Int?, action: (() -> Unit)? = null,
    ) {
        textView.apply {

            //Make Current Text of Text view as Spannable.
            val spannableString = SpannableString(this.text)

            //Assign text color as current text color or the give color.
            val textColor = color ?: this.currentTextColor

            //Make Clickable Span
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    //Perform action if there.
                    action?.invoke()
                }

                override fun updateDrawState(drawState: TextPaint) {
                    super.updateDrawState(drawState)
                    //Assign text color and underline.
                    drawState.isUnderlineText = underlined
                    drawState.color = textColor
                }
            }
            //Index of the given string in the whole string, return if index not found i.e less than 0.
            val index = spannableString.indexOf(str, ignoreCase = true)
            if (index < 0) {
                return
            }

            if (bold) {
                //If Bold, make it bold.
                spannableString.setSpan(StyleSpan(Typeface.BOLD), index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            //Set Span and Assign to tex view.
            spannableString.setSpan(clickableSpan, index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.text = spannableString
            this.movementMethod = LinkMovementMethod.getInstance()
            this.highlightColor = Color.TRANSPARENT
        }
    }

    override fun onCameraMoveStarted(reason: Int) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            val lat = googleMap!!.cameraPosition.target.latitude
            val long = googleMap!!.cameraPosition.target.longitude

            ivCurrentLocation.visible()
            tvSearchHere.visible()
            tvShowMore.gone()
            mMapPage = 1

            // Update the map coordinates when user move in the map.
            mMapCoordinates = arrayOf(lat, long)
        }
    }

    private fun performListAPICall(showLoader: Boolean) {
        mRestaurantListingViewModel.fetchRestaurants(showLoader, mPage, mResultSize,
                mCurrentCoordinates!![0], mCurrentCoordinates!![1], getSelectedFilterIds())
    }

    private fun performMapAPICall() {
        if (!mMapCoordinates.isNullOrEmpty()) {
            // Hit the API to get restaurants on current position of map
            mRestaurantListingViewModel.fetchRestaurantsForMap(mShowLoader = false, mPage = mMapPage,
                    mResultSize = mResultSize, mLatitude = mMapCoordinates!![0], mLongitude = mMapCoordinates!![1])
        }
    }

    /**
     *  Map LifeCycle Methods
     */
    override fun onStart() {
        super.onStart()
        mapRestaurants?.onStart()
    }

    override fun onResume() {
        super.onResume()
//        flShimmer.startShimmer()
        mapRestaurants?.onResume()
    }

    override fun onPause() {
//        flShimmer.stopShimmer()
        mapRestaurants?.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapRestaurants?.onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapRestaurants?.onLowMemory()
    }

    override fun onDestroyView() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        mapRestaurants?.onDestroy()
        super.onDestroyView()
    }
}