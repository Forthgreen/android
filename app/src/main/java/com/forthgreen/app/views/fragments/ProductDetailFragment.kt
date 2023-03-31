package com.forthgreen.app.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.ProductDetailViewModel
import com.forthgreen.app.views.adapters.ImagePreviewAdapter
import com.forthgreen.app.views.adapters.ProductReviewAdapter
import com.forthgreen.app.views.adapters.SuggestedProductsAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.dialog_product_detail_menu.tvCancel
import kotlinx.android.synthetic.main.dialog_review_menu.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Chetan Tuteja on 15-May-20.
 */
class ProductDetailFragment : BaseRecyclerViewFragment(),
        ProductReviewAdapter.ProductReviewClickListener, LoadMoreListener,
        SuggestedProductsAdapter.SuggestedProductsCallback {

    companion object {
        const val TAG = "ProductDetailFragment"
        const val BOOKMARK_INTENT_STATUS_UPDATED = "BOOKMARK_INTENT_STATUS_UPDATED"
        const val LOCAL_INTENT_PRODUCT_BOOKMARKED = "LOCAL_INTENT_PRODUCT_BOOKMARKED"
        private const val BUNDLE_EXTRAS_PRODUCT = "product"

        fun newInstance(product: Product): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRAS_PRODUCT, product)
                }
            }
        }
    }

    //Variables
    private var page = 1
    private var bookmarkProductStatus = SimilarProduct()
    private var isSimilarProductAction: Boolean = false
    private lateinit var product: Product
    private val mAdapter by lazy {
        ProductReviewAdapter(this, this)
    }

    private val mProductDetailViewModel: ProductDetailViewModel by lazy {
        ViewModelProvider(this).get(ProductDetailViewModel::class.java)
    }

    private val mSuggestedProductsAdapter by lazy {
        SuggestedProductsAdapter(this)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_product_detail

    override val viewModel: BaseViewModel?
        get() = mProductDetailViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = mAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = null

    override val isShowRecyclerViewDivider: Boolean
        get() = true

    override fun setData() {
        // get brand data from arguments
        product = arguments!!.getParcelable(BUNDLE_EXTRAS_PRODUCT)!!

        setupViews()
        setupListeners()
        observeProperties()

        // report product visit for statics
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn())
            mProductDetailViewModel.reportProductVisit(product._id)

        // get product list
        mProductDetailViewModel.getProductDetail(
                true,
                page,
                ProductReviewAdapter.RESULTS_PER_PAGE,
                product._id
        )
    }

    private fun setupViews() {
        //Set PushDownAnim
        PushDownAnim.setPushDownAnimTo(btnWebsite, ivProductImage)

        //Setup toolbar title and end action.
        tvToolbarTitle.text = product.brandName
        ivToolbarActionEnd.visible()
        ivToolbarActionEnd.setImageResource(R.drawable.ic_share)

        // Setup Suggested Products RecyclerView
        rvSuggestedProducts.apply {
            adapter = mSuggestedProductsAdapter
            layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        updateViews(product)

        // Register Broadcast Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(BOOKMARK_INTENT_STATUS_UPDATED)
                })
    }

    private fun setupListeners() {
        ivToolbarActionEnd.setOnClickListener {
            //Show Dialog in case of Guest mode else perform action.
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                showGuestModeDialog()
            } else {
                GeneralFunctions.shareGenericDeepLink(
                        requireContext(),
                        product.name,
                        product.info,
                        GeneralFunctions.getResizedImage(
                                ValueMapping.getPathSmall(),
                                product.coverImage
                        ),
                        Gson().toJson(product),
                        ValueMapping.deepLinkingTypeProduct()
                )
            }
        }

        btnWebsite.setOnClickListener {
            if (product.link.isNotEmpty()) {
                val url =
                        if (!product.link.startsWith("https://") && !product.link.startsWith("http://")) {
                            "https://" + product.link
                        } else product.link
                val webpage: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                    // report website visit for statics
                    if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn())
                        mProductDetailViewModel.reportWebsiteClick(product._id)
                } else {
                    showMessage(R.string.website_not_available_error)
                }
            } else {
                showMessage(R.string.website_not_available_error)
            }
        }

        ivProductImage.setOnClickListener {
            if (product.brandRef.isNotEmpty()) {
                val brand = Brand(
                        _id = product.brandRef,
                        brandName = product.brandName,
                        logo = product.logo,
                        coverImage = product.coverImage
                )
                performFragTransaction(
                        BrandDetailFragment.newInstance(brand),
                        BrandDetailFragment.TAG,
                        enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
            }
        }

        cbBookmark.setOnClickListener {
            cbBookmark.isChecked = !cbBookmark.isChecked

            GeneralFunctions.vibratePhone(requireContext())
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                showGuestModeDialog()
            } else {
                mProductDetailViewModel.addBookmark(
                        true,
                        product._id,
                        ValueMapping.onProductBookmarkAction(),
                        !cbBookmark.isChecked
                )
            }
        }

        tvWriteReview.setOnClickListener {
            //Show Dialog in case of Guest mode else perform action.
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            } else {
                performFragTransaction(
                        ProductReviewFragment.newInstance(product._id),
                        ProductReviewFragment.TAG,
                        enterAnim = R.anim.slide_up, exitAnim = R.anim.fade_out,
                        popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_down
                )
            }
        }
    }

    private fun observeProperties() {
        mProductDetailViewModel.onReceivingDetail()
                .observe(viewLifecycleOwner, Observer { productDetails ->
                    //If second page is loaded don't update the initial setup Views
                    if (page == 1) {
                        updateViews(productDetails.data)
                        product = productDetails.data
                        if (productDetails.data.similarProducts.isNotEmpty()) {
                            groupSuggestedProducts.visible()
                            mSuggestedProductsAdapter.submitList(productDetails.data.similarProducts)
                        } else {
                            groupSuggestedProducts.gone()
                        }
                    }
                    mAdapter.receiveList(
                            productDetails.data.ratingAndReview,
                            page,
                            mProductDetailViewModel.fetchUserId()
                    )
                })
        mProductDetailViewModel.onSaveActionSuccess()
                .observe(viewLifecycleOwner, { saveActionSuccess ->
                    if (saveActionSuccess) {
                        // Send Common Local Intent as BrandDetail Fragment
                        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                                Intent(BrandDetailFragment.LOCAL_INTENT_BRAND_FOLLOWED)
                        )
                    }
                })
        mProductDetailViewModel.onBookmarkAddSuccess().observe(viewLifecycleOwner, { success ->
            if (success) {
                if (isSimilarProductAction) {
                    isSimilarProductAction = false
                    mSuggestedProductsAdapter.updateProduct(bookmarkProductStatus.copy(isBookmark = !bookmarkProductStatus.isBookmark))
                } else {
                    cbBookmark.isChecked = !cbBookmark.isChecked
                    // Send Broadcast
                    LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(Intent(BOOKMARK_INTENT_STATUS_UPDATED))
                }
                // Send Broadcast
                LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(Intent(LOCAL_INTENT_PRODUCT_BOOKMARKED))
            }
        })
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                onPullDownToRefresh()
            }
        }
    }

    private fun updateViews(product: Product) {
        tvToolbarTitle.text = product.brandName
        tvProductNameTop.text = product.name
        rbProductRating.rating = product.averageRating.toFloat()
        tvRatingNumber.text = product.ratingAndReview.size.toString()
        tvProductName.text = product.name
        tvProductPrice.text = product.productPriceText
        tvProductDesc.text = product.info
        cbBookmark.isChecked = product.isBookmark

        viewPager.adapter =
                ImagePreviewAdapter(childFragmentManager, product.images as ArrayList<String>)
        circlePagerIndicator!!.setViewPager(viewPager)

        val minURL = GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), product.logo)
        Glide.with(this)
                .load(minURL)
                .placeholder(R.drawable.ic_brand_placeholder)
                .error(R.drawable.ic_brand_placeholder)
                .into(ivProductImage)

    }

    override fun performMenuClick(review: RatingAndReview) {
        //Show Custom Dialog by inflating Layout using MaterialDialog Library.
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
                //Show Dialog in case of Guest mode else perform action.
                if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                    showGuestModeDialog()
                } else {
                    performFragTransaction(
                            ReportReviewFragment.newInstance(review._id, true),
                            ReportReviewFragment.TAG
                    )
                }
            }
            tvCancel.setOnClickListener {
                this.dismiss()
            }
        }
    }

    override fun openUserProfile(review: RatingAndReview) {
        performFragTransaction(
                OtherUserProfileFragment.newInstance(
                        UserProfile(
                                firstName = review.fullName,
                                image = review.image, _id = review.userRef
                        )
                ),
                OtherUserProfileFragment.TAG
        )
    }

    private fun showGuestModeDialog() {
        performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
    }

    /**
     * @description Helper method to make text view Spannable.
     * @param textView Text view on which to perform change.
     * @param str part of string on which to perform change.
     * @param underlined whether the text should be underlined.
     * @param bold whether the text should be bold.
     * @param action Action to be performed onClick.
     */
    private fun makeTextLink(
            textView: TextView,
            str: String,
            underlined: Boolean,
            bold: Boolean,
            color: Int?,
            action: (() -> Unit)? = null,
    ) {
        //Make Current Text of Text view as Spannable.
        val spannableString = SpannableString(textView.text)

        //Assign text color as current text color or the give color.
        val textColor = color ?: textView.currentTextColor

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
        //Index of the given string in the whole string.
        val index = spannableString.indexOf(str)
        if (bold) {
            //If Bold, make it bold.
            spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    index,
                    index + str.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        //Set Span and Assign to tex view.
        spannableString.setSpan(
                clickableSpan,
                index,
                index + str.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

    override fun onSuggestedProductsClick(product: SimilarProduct) {
        // Perform Transaction to selected Product.
        product.apply {
            performFragTransaction(
                    newInstance(
                            Product(
                                    _id = _id,
                                    name = name,
                                    brandName = brandName,
                                    brandRef = brandRef,
                                    price = price,
                                    link = link,
                                    images = images,
                                    info = info
                            )
                    ), TAG, enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
            )
        }
    }

    override fun updateFollowStatus(product: SimilarProduct) {
        // Vibrate phone
        GeneralFunctions.vibratePhone(requireContext())
        //Show Dialog in Case of Guest User else perform action.
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            showGuestModeDialog()
        } else {
            bookmarkProductStatus = product
            isSimilarProductAction = true
            mProductDetailViewModel.addBookmark(
                    true,
                    product._id,
                    ValueMapping.onProductBookmarkAction(),
                    !product.isBookmark
            )
        }
    }

    override fun onPullDownToRefresh() {
        page = 1
        mProductDetailViewModel.getProductDetail(
                false,
                page,
                ProductReviewAdapter.RESULTS_PER_PAGE,
                product._id
        )
    }

    override fun onLoadMore() {
        page++
        mProductDetailViewModel.getProductDetail(
                false,
                page,
                ProductReviewAdapter.RESULTS_PER_PAGE,
                product._id
        )
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }
}
