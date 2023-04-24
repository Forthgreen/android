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
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.BrandDetailViewModel
import com.forthgreen.app.views.adapters.ProductListAdapter
import com.forthgreen.app.views.adapters.ProductListIemDecoration
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_brand_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class BrandDetailFragment : BaseRecyclerViewFragment(), LoadMoreListener,
        ProductListAdapter.ProductDetailCallback {

    companion object {
        const val TAG = "BrandDetailFragment"
        const val LOCAL_INTENT_BRAND_FOLLOWED = "FollowedBrand"
        const val LOCAL_INTENT_BRAND_BOOKMARKED = "LOCAL_INTENT_BRAND_BOOKMARKED"
        const val BUNDLE_EXTRAS_BRAND = "brand"
        private const val MAX_LINES_LIMIT = 3

        fun newInstance(brand: Brand): BrandDetailFragment {
            return BrandDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRAS_BRAND, brand)
                }
            }
        }
    }

    // Variables
    private var bookmarkProductStatus = Product()
    private var isProductAction: Boolean = false
    private lateinit var brand: Brand
    private val productListAdapter by lazy { ProductListAdapter(this, false) }
    private val mBrandDetailViewModel: BrandDetailViewModel by lazy {
        ViewModelProvider(this).get(BrandDetailViewModel::class.java)
    }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(requireContext())
    }

    private lateinit var mGridlayoutManager: GridLayoutManager

    override val layoutId: Int
        get() = R.layout.fragment_brand_detail

    override val viewModel: BaseViewModel?
        get() = mBrandDetailViewModel

    override val recyclerViewAdapter: RecyclerView.Adapter<*>?
        get() = productListAdapter

    override val layoutManager: RecyclerView.LayoutManager?
        get() = mGridlayoutManager

    override val isShowRecyclerViewDivider: Boolean
        get() = false

    override fun setData() {
        setUpViews()
        setUpListeners()
        observeProperties()
    }

    private fun setUpViews() {
        // Get brand data from arguments
        brand = arguments!!.getParcelable(BUNDLE_EXTRAS_BRAND)!!

        // Set Push down animation
        PushDownAnim.setPushDownAnimTo(btnWebsite)

        // Initialise layout manager
        mGridlayoutManager = GridLayoutManager(activity, 2)
        //Return span of 2 in case of loadMore so that it can occupy whole space instead of half.
        mGridlayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (productListAdapter.getItemViewType(position)) {
                    ProductListAdapter.ROW_TYPE_LOAD_MORE -> 2
                    else -> 1
                }
            }
        }

        // Set toolbar
        tvToolbarTitle.text = brand.brandName
        ivToolbarActionEnd.apply {
            setImageResource(R.drawable.ic_share)
            visible()
        }

        updateViews()

        recyclerView.apply {
            val productSpacing = resources.getDimensionPixelSize(R.dimen.home_product_offset)
            addItemDecoration(ProductListIemDecoration(productSpacing, 2))
        }

        // Get product list
        mBrandDetailViewModel.getBrandDetail(true, brand._id)

        // Register Broadcast Receiver
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                IntentFilter().apply {
                    addAction(ProductDetailFragment.BOOKMARK_INTENT_STATUS_UPDATED)
                })
    }

    private fun observeProperties() {
        mBrandDetailViewModel.onReceivingDetail().observe(viewLifecycleOwner, Observer {
            brand = it.data
            updateViews()
        })
        mBrandDetailViewModel.onFollowBrandSuccess()
                .observe(viewLifecycleOwner, Observer { followActionSuccess ->
                    if (followActionSuccess) {
                    }
                    //Send Local Broadcast
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                            Intent(LOCAL_INTENT_BRAND_FOLLOWED)
                    )
                })
        mBrandDetailViewModel.onBookmarkAddSuccess().observe(viewLifecycleOwner, { success ->
            if (success) {
                if (isProductAction) {
                    isProductAction = false
                    productListAdapter.updateProduct(bookmarkProductStatus.copy(isBookmark = !bookmarkProductStatus.isBookmark))
                    // Send Broadcast
                    LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(Intent(ProductDetailFragment.LOCAL_INTENT_PRODUCT_BOOKMARKED))
                } else {
                    cbBookmark.isChecked = !cbBookmark.isChecked
                    // Send Broadcast
                    LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(Intent(LOCAL_INTENT_BRAND_BOOKMARKED))
                }
            }
        })
    }

    private fun updateViews() {
        tvBrandName.text = brand.brandName
        ivBrandImage.loadURL(brand.coverImage, false)
        cbBookmark.isChecked = brand.isBookmark
        if (brand.about.isNotEmpty()) {
            tvBrandDetail.apply {
                visible()
                // Set Default Max Lines Limit
                maxLines = MAX_LINES_LIMIT
                text = brand.about
                viewTreeObserver.addOnGlobalLayoutListener(brandDetailLayoutListener)
            }
            if (tvBrandDetail.lineCount > MAX_LINES_LIMIT && tvBrandDetail.maxLines <= MAX_LINES_LIMIT) {
                tvViewMore.visible()
            } else {
                tvViewMore.gone()
            }
        } else {
            tvBrandDetail.gone()
            tvViewMore.gone()
        }
        // load brand image
        val minURL = GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), brand.logo)
        Glide.with(this)
                .load(minURL)
                .placeholder(R.drawable.ic_brand_placeholder)
                .error(R.drawable.ic_brand_placeholder)
                .into(civBrandIcon)

        // Send list of products
        productListAdapter.updateList(brand.products, brand.page, false, brand.brandName)
    }

    private fun setUpListeners() {
        //showing options
        ivToolbarActionEnd.setOnClickListener {
//            MaterialDialog(requireContext()).show {
//                customView(R.layout.dialog_fragment_brand_detail_options, dialogWrapContent = true, noVerticalPadding = true, horizontalPadding = false)
//                cancelable(false)
//                cancelOnTouchOutside(true)
//                cornerRadius(res = R.dimen.dialog_corner_radius)
//                tvShare.setOnClickListener {
//                    this.dismiss()
            //Show Dialog in case of Guest mode else perform action.
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                showGuestModeDialog()
            } else {
                GeneralFunctions.shareGenericDeepLink(
                        requireContext(),
                        brand.brandName,
                        brand.about,
                        GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), brand.logo),
                        Gson().toJson(brand),
                        ValueMapping.deepLinkingTypeBrand()
                )
            }
//                }
//                tvReport.setOnClickListener {
//                    this.dismiss()
//                    //Show Dialog in Case of Guest Mode else hit API
//                    if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
//                        showGuestModeDialog()
//                    } else {
//                        performTransaction(ReportBrandFragment.newInstance(brand._id), ReportBrandFragment.TAG)
//                    }
//                }
//                tvCancel.setOnClickListener {
//                    this.dismiss()
//                }
//            }
        }

        btnWebsite.setOnClickListener {
            if (brand.website.isNotEmpty()) {
                val url =
                        if (!brand.website.startsWith("https://") && !brand.website.startsWith("http://")) {
                            "https://" + brand.website
                        } else brand.website
                val webpage: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    showMessage(R.string.website_not_available_error)
                }
            } else {
                showMessage(R.string.website_not_available_error)
            }
        }

        tvViewMore.setOnClickListener {
            // Set Max Lines value to the maximum.
            tvBrandDetail.maxLines = Integer.MAX_VALUE
            tvViewMore.gone()
        }

        cbBookmark.setOnClickListener {
            cbBookmark.isChecked = !cbBookmark.isChecked

            GeneralFunctions.vibratePhone(requireContext())
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                showGuestModeDialog()
            } else {
                mBrandDetailViewModel.addBookmark(
                        true,
                        brand._id,
                        ValueMapping.onBrandBookmarkAction(),
                        !cbBookmark.isChecked
                )
            }
        }
    }

    // Local Broadcast receiver
    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                onPullDownToRefresh()
            }
        }
    }

    private val brandDetailLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            try {
                tvBrandDetail.viewTreeObserver.removeOnGlobalLayoutListener(this)
                /*
                     * If current line count is greater than limit and we have
                     * not already set the maxLines maximum value, then make ViewMore visible.
                     * If we have already set a max value, then we do not need to show it.
                     */
                if (tvBrandDetail.lineCount > MAX_LINES_LIMIT && tvBrandDetail.maxLines <= MAX_LINES_LIMIT) {
                    tvViewMore.visible()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Show Guest Mode Dialog with help of Material Dialog by inflating custom layout.
    private fun showGuestModeDialog() {
        callUserLoginDialog()
       // performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
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

    private fun callUserLoginDialog() {
        val userLoginDialog = UserLoginDialog()
        userLoginDialog.showUserLoginDialog(requireActivity() as AppCompatActivity, object :
            LoginButtonClickInterface {
            override fun loginButtonClick() {
                performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            }
        })
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

    override fun onPullDownToRefresh() {
        mBrandDetailViewModel.getBrandDetail(false, brand._id)
    }

    override fun onLoadMore() {
        mBrandDetailViewModel.getBrandDetail(false, brand._id)
    }

    override fun showProductDetail(product: Product) {
        product.brandRef = brand._id
        performFragTransaction(
                ProductDetailFragment.newInstance(product), ProductDetailFragment.TAG,
                enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
        )
    }

    override fun updateFollowStatus(product: Product) {
        // Vibrate phone
        GeneralFunctions.vibratePhone(requireContext())
        //Show Dialog in Case of Guest User else perform action.
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
            showGuestModeDialog()
        } else {
            bookmarkProductStatus = product
            isProductAction = true
            mBrandDetailViewModel.addBookmark(
                    true,
                    product._id,
                    ValueMapping.onProductBookmarkAction(),
                    !product.isBookmark
            )
        }
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }
}