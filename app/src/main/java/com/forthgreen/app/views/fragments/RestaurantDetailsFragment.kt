package com.forthgreen.app.views.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Restaurant
import com.forthgreen.app.repository.models.ReviewData
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.RestaurantDetailsViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.RestaurantReviewsAdapter
import com.forthgreen.app.views.adapters.ViewPagerAdapter
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.trimmedText
import com.forthgreen.app.views.utils.visible
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_product_detail_menu.tvCancel
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_restaurant_details.*
import kotlinx.android.synthetic.main.toolbar.*

class RestaurantDetailsFragment : BaseRecyclerViewFragment(), View.OnClickListener,
    RestaurantReviewsAdapter.MenuClickCallback {

    companion object {
        const val TAG = "RestaurantDetailsFragment"
        private const val resultSize = 250
        private const val BUNDLE_EXTRAS_DETAILS = "restaurantDetails"
        const val LOCAL_INTENT_RESTAURANT_FOLLOWED = "FollowedRestaurant"
        const val LOCAL_INTENT_RESTAURANT_BOOKMARKED = "LOCAL_INTENT_RESTAURANT_BOOKMARKED"

        fun newInstance(restaurant: Restaurant): RestaurantDetailsFragment {
            return RestaurantDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRAS_DETAILS, restaurant)
                }
            }
        }
    }

    //variables
    private var page = 1
    private val mRestaurantDetailsViewModel by lazy {
        //getting viewModel
        ViewModelProvider(this).get(RestaurantDetailsViewModel::class.java)
    }
    private val mViewPagerAdapter by lazy { ViewPagerAdapter() }
    private val mAdapter by lazy { RestaurantReviewsAdapter(this) }
    private lateinit var mDetails: Restaurant

//    // Create a new PlacesClient instance
//    private val placesClient = Places.createClient(requireActivity())
//    // Type of Data to return via Places AutoComplete
//    private val placeFields = listOf(Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET)

    override val layoutId: Int
        get() = R.layout.fragment_restaurant_details

    override val viewModel: BaseViewModel?
        get() = mRestaurantDetailsViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        mDetails = arguments?.getParcelable(BUNDLE_EXTRAS_DETAILS)!!

        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        //setup toolbar
        tvToolbarTitle.text = mDetails.name
        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_share)
            visible()
        }
        //setup ViewPager adapter
        viewPager.adapter = mViewPagerAdapter
        dotIndicator.setViewPager2(viewPager)

        //push sown anim
        PushDownAnim.setPushDownAnimTo(btnWebsite, tvPhoneNo)

        //update views
        updateViews(mDetails)

        //fetch details via viewModel
        mRestaurantDetailsViewModel.getRestaurantDetails(false, mDetails._id)
//        if (!Places.isInitialized()) {
//            Places.initialize(requireContext(), getString(R.string.google_places_key))
//        }
//        val request = FetchPlaceRequest.newInstance(getString(R.string.google_places_key), placeFields)
//
//        placesClient.fetchPlace(request)
//            .addOnSuccessListener { response: FetchPlaceResponse ->
//                val place = response.place
//                tvOpenNow.text = "OpenNow: ${place.openingHours}"
//            }.addOnFailureListener { exception: Exception ->
//                if (exception is ApiException) {
//                }
//            }
        //fetch reviews for restaurant
        mRestaurantDetailsViewModel.getRestaurantReviews(false, page, resultSize, mDetails._id)
    }

    private fun setupListeners() {
        ivToolbarActionEnd.setOnClickListener(this)
        tvWriteReview.setOnClickListener(this)
        btnWebsite.setOnClickListener(this)
        tvPhoneNo.setOnClickListener(this)
        ivMapView.setOnClickListener(this)
        tvAddress.setOnLongClickListener {
            (requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(ClipData.newPlainText("Address", tvAddress.trimmedText))
            }
            showMessage(resId = R.string.address_copied_success_message)
            true
        }
        cbBookmark.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivToolbarActionEnd -> {
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                    performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                } else {
                    GeneralFunctions.shareGenericDeepLink(
                        mContext = requireContext(),
                        title = mDetails.name,
                        description = mDetails.about,
                        imageUrl = GeneralFunctions.getResizedImage(
                            ValueMapping.getPathSmall(),
                            mDetails.images.first()
                        ),
                        payload = Gson().toJson(mDetails),
                        payloadMapping = ValueMapping.deepLinkingTypeRestaurant()
                    )
                }
            }
            R.id.tvPhoneNo -> {
                val uri = Uri.parse("tel:" + tvPhoneNo.text.toString().trim())
                startActivity(Intent(Intent.ACTION_DIAL, uri))
            }
//            R.id.btnFollow -> {
//                //Show Dialog in Case of Guest User else perform action.
//                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
//                    //Set button status to false
//                    btnFollow.isChecked = false
//                    performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
//                } else {
//                    //Invert Status to original
//                    btnFollow.isChecked = !btnFollow.isChecked
//
//                    //If it is already checked, hit for un-follow action else follow.
//                    if (btnFollow.isChecked) {
//                        mRestaurantDetailsViewModel.updateFollowStatus(true, status = false, restaurantRef = mDetails._id)
//                    } else {
//                        mRestaurantDetailsViewModel.updateFollowStatus(true, status = true, restaurantRef = mDetails._id)
//                    }
//                }
//            }
//            R.id.btnReview -> {
//                //Show Dialog in case of Guest mode else perform action.
//                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
//                    performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
//                } else {
//                    performTransaction(RestaurantReviewFragment.newInstance(mDetails._id), RestaurantReviewFragment.TAG)
//                }
//            }
            R.id.cbBookmark -> {
                cbBookmark.isChecked = !cbBookmark.isChecked

                GeneralFunctions.vibratePhone(requireContext())
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                    showGuestModeDialog()
                } else {
                    mRestaurantDetailsViewModel.addBookmark(
                        true,
                        mDetails._id,
                        ValueMapping.onRestaurantBookmarkAction(),
                        !cbBookmark.isChecked
                    )
                }
            }
            R.id.tvWriteReview -> {
                //Show Dialog in case of Guest mode else perform action.
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                    performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                } else {
                    performFragTransaction(
                        RestaurantReviewFragment.newInstance(mDetails._id),
                        RestaurantReviewFragment.TAG,
                        enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                    )
                }
            }
            R.id.btnWebsite -> {
                if (mDetails.website.isNotEmpty()) {
                    val url =
                        if (!mDetails.website.startsWith("https://") && !mDetails.website.startsWith(
                                "http://"
                            )
                        ) {
                            "https://" + mDetails.website
                        } else mDetails.website
                    val webPage: Uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, webPage)
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        showMessage(R.string.website_not_available_error)
                    }
                } else {
                    showMessage(R.string.website_not_available_error)
                }
            }
            R.id.ivMapView -> {
                if (mDetails.location.coordinates.isNotEmpty()) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                GeneralFunctions.openLocationInMap(mDetails.location.coordinates)
                            )
                        )
                    )
                }
            }
        }
    }

    private fun observeProperties() {
        mRestaurantDetailsViewModel.onReceivedDetails()
            .observe(viewLifecycleOwner, Observer { fetchedDetails ->
                updateViews(fetchedDetails.data)
                mDetails = fetchedDetails.data
            })
        mRestaurantDetailsViewModel.onReceivedReviews()
            .observe(viewLifecycleOwner, Observer { fetchedReviews ->
                if (fetchedReviews.data.isNotEmpty()) {
                    mAdapter.submitList(
                        fetchedReviews.data,
                        mUserId = mRestaurantDetailsViewModel.fetchUserId()
                    )
                } else {
                    mAdapter.submitList(emptyList())
                }
            })
//        mRestaurantDetailsViewModel.onFollowStatusUpdated().observe(viewLifecycleOwner, Observer { updateSuccess ->
//            if (updateSuccess != null) {
//                //If Action is successful change the state of button
//                btnFollow.isChecked = !btnFollow.isChecked
//                showMessage(resId = R.string.saved_status_updated)
//            }
//            //Send Local Broadcast
//            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
//                    Intent(LOCAL_INTENT_RESTAURANT_FOLLOWED)
//            )
//        })
        mRestaurantDetailsViewModel.onBookmarkAddSuccess().observe(viewLifecycleOwner, { success ->
            if (success) {
                cbBookmark.isChecked = !cbBookmark.isChecked
                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(LOCAL_INTENT_RESTAURANT_BOOKMARKED))
            }
        })
    }

    private fun updateViews(restaurant: Restaurant) {
        restaurant.apply {
            ivRestaurantImage.loadURL(thumbnail, false)
            tvRestaurantName.text = name
            rattingBar.rating = ratings.averageRating.toFloat()
            tvRatingCount.text = "${ratings.count}"
            tvCuisine.text = typeOfFood
            tvDescription.text = about
            if (phoneNumber.isNotEmpty() && showPhoneNumber) {
                groupPhoneNumber.visible()
                tvPhoneNo.text = phoneCode + phoneNumber
            } else {
                groupPhoneNumber.gone()
            }
            tvAddress.text = location.address
            tvAddressDesc.text = portCode
            ivMapView.loadURL(placePicture, false)
            // set price range
            val spanPrice: Spannable = SpannableString("$$$")
            spanPrice.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorFollowingButton
                    )
                ), 0, price.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvPriceRange.text = spanPrice
            cbBookmark.isChecked = isBookmark
            tvDistance.text = "${GeneralFunctions.getDistanceInMiles(distance)} miles"

            mViewPagerAdapter.submitList(images)
        }
    }

    fun ImageView.loadURL(imageURL: String, isUserImage: Boolean) {
        //Get complete Image URL using Identifier received.
        val bestURL = GeneralFunctions.getResizedImage(ValueMapping.getPathBest(), imageURL)

        if (isUserImage) {
            Glide.with(this)
                .load(bestURL)
                .thumbnail(Glide.with(this).load(bestURL))
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(this)
        } else {
            Glide.with(this)
                .load(bestURL)
                .thumbnail(Glide.with(this).load(bestURL))
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimension(R.dimen.rounded_radius).toInt())
                )
                .placeholder(R.drawable.ic_placeholder_broken_img)
                .error(R.drawable.ic_placeholder_broken_img)
                .into(this)
        }
    }

    override fun onPullDownToRefresh() {
    }

    //Show Guest Mode Dialog with help of Material Dialog by inflating custom layout.
    private fun showGuestModeDialog() {
        performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
//        MaterialDialog(requireContext()).show {
//            customView(R.layout.dialog_fragment_brand_detail, dialogWrapContent = true, noVerticalPadding = true, horizontalPadding = false)
//            cancelable(false)
//            cancelOnTouchOutside(false)
//            cornerRadius(res = R.dimen.dialog_corner_radius)
//            //Set PushDown Anim and Make text Bold for Login Spannable
//            PushDownAnim.setPushDownAnimTo(btnSignUp)
//            btnSignUp.setOnClickListener {
//                performTransaction(SignUpFragment(), SignUpFragment.TAG)
//                this.dismiss()
//            }
//            ivCancel.setOnClickListener { this.dismiss() }
//            tvHaveAccount.setOnClickListener {
//                performTransaction(LoginFragment(), LoginFragment.TAG)
//                this.dismiss()
//            }
//        }
    }

    override fun openMenuOptions(review: ReviewData) {
        MaterialDialog(requireContext()).show {
            customView(
                R.layout.dialog_review_menu,
                dialogWrapContent = true,
                noVerticalPadding = true
            )
            cancelOnTouchOutside(false)
            cancelable(false)
            cornerRadius(res = R.dimen.dialog_corner_radius)
            tvReportAbuse.setOnClickListener {
                this.dismiss()
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                    performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                } else {
                    performTransaction(
                        ReportReviewFragment.newInstance(review._id, false),
                        ReportReviewFragment.TAG
                    )
                }
            }
            tvCancel.setOnClickListener {
                this.dismiss()
            }
        }
    }

    override fun openUserProfile(user: UserProfile) {
        performFragTransaction(
            OtherUserProfileFragment.newInstance(user),
            OtherUserProfileFragment.TAG
        )
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(
            supportFragmentManager(),
            R.id.flFragContainer,
            frag,
            fragmentTag,
            isAddFragment = true
        )
    }
}