package com.forthgreen.app.views.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.*
import com.forthgreen.app.services.MyFirebaseMessagingService
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.GeneralFunctions.INTENT_DEEP_LINK_TIME_STAMP
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.adapters.ViewPagerAdapterCommon
import com.forthgreen.app.views.dialogfragments.UserLoginDialog
import com.forthgreen.app.views.fragments.*
import com.forthgreen.app.views.interfaces.DrawerCallbacks
import com.forthgreen.app.views.interfaces.LoginButtonClickInterface
import com.google.gson.Gson
import io.branch.referral.Branch
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseAppCompactActivity(), DrawerCallbacks {

    companion object {
        const val TAG = "MainActivity"
    }

    // Variables
    private var prevMenuItem: MenuItem? = null        // To keep track of previous menu item selected.
    private val mShopFragment by lazy { ShopCategoriesFragment() }
    private val mPostFeedFragmentGuestUser by lazy { PostsFeedFragment() }
    private val mPostFeedFragment by lazy { PostFeedFilterTabFragment() }
    private val mRestaurantFragment by lazy { RestaurantListingFragment() }
    private val mMyStuffFragment by lazy { MyStuffFragment() }
    private val mSelfProfileFragment by lazy { SelfProfileFragment() }

    private val mLocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(baseContext)
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun init() {
        // check if app is restarted from launcher icon then close redundant activity
        if (!isTaskRoot
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.action != null
                && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }

        // Sets default icon tint to null for BottomNavBar to use custom tints
        navbar.itemIconTintList = null
        setupViews()
        setupViewpager()
        setupListeners()
        checkForIntents()
    }

    private fun setupViews() {
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()
                && ApplicationGlobal.showOnBoardings) {
            performTransaction(OnBoardingsFragment(), OnBoardingsFragment.TAG)
        } else if (!ApplicationGlobal.showOnBoardings) {
            // If showOnBoardings is false, it implies we just logged-out so show Welcome Screen
            // and skip OnBoardings and mark true for next instance.
            performTransaction(WelcomeFragment.newInstance(false),
                    WelcomeFragment.TAG)
            ApplicationGlobal.showOnBoardings = true
        }

        // Lock ViewPager
        vpBottomNav.setSwipePagingEnabled(swipeEnabled = false)


        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            // Register Local Broadcast Receiver
            mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,
                    IntentFilter(MyFirebaseMessagingService.LOCAL_INTENT_NEW_PUSH_NOTIFICATION)
            )
        }
    }

    private fun setupViewpager() {
        // Add fragments via the adapter
        try {
            unlockNavDrawer()
            val mAdapter = ViewPagerAdapterCommon(supportFragmentManager)
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessGuest()) {
                //  performFragTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
                mAdapter.addFrag(mPostFeedFragmentGuestUser)
            }else {
                mAdapter.addFrag(mPostFeedFragment)
            }
           // mAdapter.addFrag(mPostFeedFragment)
            mAdapter.addFrag(mShopFragment)
            mAdapter.addFrag(mRestaurantFragment)
            if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
                mAdapter.addFrag(mMyStuffFragment)
                mAdapter.addFrag(mSelfProfileFragment)
            } else {
               /* repeat(2) {
                   // mAdapter.addFrag(WelcomeFragment.newInstance(true))
                    mAdapter.addFrag(WelcomeFragment.newInstance(false))
                }*/
                mAdapter.addFrag(mMyStuffFragment)
                mAdapter.addFrag(mSelfProfileFragment)
            }
            lockNavDrawer()     // Lock Nav Drawer by default.
            vpBottomNav.adapter = mAdapter
            vpBottomNav.offscreenPageLimit = 5
        } catch (e: Exception) {
            Log.d(TAG, "setupViewPager: Error")
        }
    }

    private fun setupListeners() {
        // Set Bottom navigation bar to work with ViewPager

        // If you are making any changes in order of ViewPager tabs, make sure to change it in
        // onBackPress override below too.
        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    lockNavDrawer()
                    if (vpBottomNav.currentItem == 0) {
                        vpBottomNav.setCurrentItem(0, false)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(
                            PostsFeedFragment.LOCAL_INTENT_SCROLL_TO_TOP
                        ))
                      //  mPostFeedFragment.scrollToTop()
                    } else {
                        vpBottomNav.setCurrentItem(0, false)
                       // mPostFeedFragment.showShimmer()
                    }
                    vpBottomNav.setCurrentItem(0, false)
                    try {
                        if (mPostFeedFragment.childFragmentManager.backStackEntryCount > 0) {
                            mPostFeedFragment.childFragmentManager.popBackStack(null,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    true
                }
                R.id.nav_shop_page -> {
                    lockNavDrawer()
                    if (vpBottomNav.currentItem == 1) {
                        mShopFragment.scrollToTop()
                    } else {
                        mShopFragment.showShimmer()
                    }
                    vpBottomNav.setCurrentItem(1, false)
                    true
                }
                R.id.nav_restaurant -> {
                    lockNavDrawer()
                    if (vpBottomNav.currentItem == 2) {
                        mRestaurantFragment.scrollToTop()
                    } else {
                        mRestaurantFragment.showShimmer()
                    }
                    vpBottomNav.setCurrentItem(2, false)
                    true
                }
                R.id.nav_bookmark -> {
                    lockNavDrawer()
                    if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn() && vpBottomNav.currentItem != 3) {
                        mMyStuffFragment.showShimmer()
                    }
//                    toggleNotificationBadge(showBadge = false)
                    vpBottomNav.setCurrentItem(3, false)
                    true
                }
                R.id.nav_profile -> {
                    unlockNavDrawer()
                    if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn() && vpBottomNav.currentItem != 4) {
                        mSelfProfileFragment.showShimmer()
                    }
                    vpBottomNav.setCurrentItem(4, false)
                    true
                }
            }
            false
        }

        // Set ViewPager changes to reflect upon BottomNavBar
        vpBottomNav.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    navbar.menu.getItem(0).isChecked = false
                }

                navbar.menu.getItem(position).isChecked = true
                prevMenuItem = navbar.menu.getItem(position)
            }
        })
    }

    fun showMyBrandsIndicator(isShow: Boolean) {
        /*val badge: BadgeDrawable = navbar.getOrCreateBadge(
                R.id.nav_notification)
        badge.backgroundColor = ContextCompat.getColor(this, R.color.colorGreenBrand)
        badge.isVisible = isShow*/
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        Branch.sessionBuilder(this).withCallback(branchListener).withData(this.intent?.data).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        // Branch reinit (in case Activity is already in foreground when Branch link is clicked)
        Branch.sessionBuilder(this).withCallback(branchListener).reInit()
    }

    private val branchListener = Branch.BranchReferralInitListener { referringParams, error ->
        if (error == null) {
            Log.i("BRANCH SDK", referringParams.toString())
            if (referringParams != null) {
                val timeStamp = referringParams.getString(INTENT_DEEP_LINK_TIME_STAMP)
                val payloadType = referringParams.getString(GeneralFunctions.INTENT_DEEP_LINK_PAYLOAD_MAPPING)
                if (payloadType == ValueMapping.deepLinkingTypeBrand()) {
                    val brand = Gson().fromJson(referringParams.getString(GeneralFunctions.INTENT_DEEP_LINK_PAYLOAD), Brand::class.java)
                    performTransaction(BrandDetailFragment.newInstance(brand), BrandDetailFragment.TAG)
                } else if (payloadType == ValueMapping.deepLinkingTypeProduct()) {
                    val product = Gson().fromJson(referringParams.getString(GeneralFunctions.INTENT_DEEP_LINK_PAYLOAD), Product::class.java)
                    performTransaction(ProductDetailFragment.newInstance(product), ProductDetailFragment.TAG)
                } else if (payloadType == ValueMapping.deepLinkingTypeRestaurant()) {
                    val restaurant = Gson().fromJson(referringParams.getString(GeneralFunctions.INTENT_DEEP_LINK_PAYLOAD), Restaurant::class.java)
                    performTransaction(RestaurantDetailsFragment.newInstance(restaurant), RestaurantDetailsFragment.TAG)
                } else if (payloadType == ValueMapping.deepLinkingTypeProfile()) {
                    val profile = Gson().fromJson(referringParams.getString(GeneralFunctions.INTENT_DEEP_LINK_PAYLOAD), UserProfile::class.java)
                    performTransaction(OtherUserProfileFragment.newInstance(profile), OtherUserProfileFragment.TAG)
                }

            }
            // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
            // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
        } else {
            Log.e("BRANCH SDK", error.message)
        }
    }

    private fun checkForIntents() {
        MyFirebaseMessagingService.INTENT_EXTRAS_NOTIFICATION_TYPE.let { notifTypeExtra ->
            if (intent.hasExtra(notifTypeExtra) && !wasLaunchedFromRecents()) {
                // Extract Notification type
                val notificationType = intent.getIntExtra(notifTypeExtra, 1)

                // Perform operations according to type if payload is present.
                val payload = MyFirebaseMessagingService.INTENT_EXTRAS_PAYLOAD
                if (intent.hasExtra(payload)) {
                    intent.getParcelableExtra<PushNotificationPayload>(payload)?.apply {
                        when (notificationType) {   // Perform Operation as per the type.
                            ValueMapping.onNotifPostLiked(), ValueMapping.onNotifComment(),
                            ValueMapping.onNotifReply(), ValueMapping.onNotifCommentLiked(),
                            ValueMapping.onNotifReplyLiked(),
                            -> {
                                performTransaction(NotificationDetailsFragment.newInstance(
                                    notificationId = notificationId,
                                    notificationType = notificationType),
                                    NotificationDetailsFragment.TAG)
                            }
                            ValueMapping.onNotifFollowingType() -> {
                                performTransaction(OtherUserProfileFragment.newInstance(
                                        UserProfile(_id = userRef)),
                                        OtherUserProfileFragment.TAG)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun wasLaunchedFromRecents(): Boolean {
        val flags: Int = intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
        return flags == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }

//    private fun toggleNotificationBadge(showBadge: Boolean) {
//        navbar.getOrCreateBadge(R.id.nav_bookmark).apply {
//            isVisible = showBadge
//            backgroundColor = ContextCompat.getColor(baseContext, R.color.colorNotificationBadge)
//        }
//    }

    private val mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                when (intent.action) {
                    MyFirebaseMessagingService.LOCAL_INTENT_NEW_PUSH_NOTIFICATION -> {
//                        toggleNotificationBadge(showBadge = true)
                    }
                }
            }
        }
    }

    //Drawer Callbacks
    override fun openNavDrawer() {
        if (drawerLayoutMain != null) {
            drawerLayoutMain.openDrawer(GravityCompat.START)
        }
    }

    override fun closeNavDrawer() {
        if (drawerLayoutMain != null) {
            drawerLayoutMain.closeDrawer(GravityCompat.START)
        }
    }

    override fun lockNavDrawer() {
        if (drawerLayoutMain != null) {
            drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun unlockNavDrawer() {
        if (drawerLayoutMain != null) {
            drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    // This code sucks, you know it and I know it.
    // Move on and call me an idiot later. I'm sorry.
    override fun onBackPressed() {
        // To Implement Instagram like Backstack, we find our Fragment in fragments list by
        // comparing if it is an instanceOf PostFragments and cross check its childFragmentManager
        // to see if there is any pending backstack, if so first pop it.
        supportFragmentManager.fragments.find { frag -> frag is PostsFeedFragment }?.let { postFeed ->
            if (postFeed.childFragmentManager.backStackEntryCount > 0
                    && vpBottomNav.currentItem == 0 && supportFragmentManager.backStackEntryCount == 0) {
                postFeed.childFragmentManager.popBackStack()
                return
            }
        }

        supportFragmentManager.fragments.find { frag -> frag is PostFeedFilterTabFragment }?.let { postFeed ->
            if (postFeed.childFragmentManager.backStackEntryCount > 0
                && vpBottomNav.currentItem == 0 && supportFragmentManager.backStackEntryCount == 0) {
                postFeed.childFragmentManager.popBackStack()
                return
            }
        }

        supportFragmentManager?.apply {
            if (backStackEntryCount == 1 && (findFragmentByTag(OnBoardingsFragment.TAG) != null)) {
                finish()
            } else if (backStackEntryCount > 0) {
                popBackStack()
            } else if (fragments.isNotEmpty() && fragments[fragments.size - 1].tag == "com.bumptech.glide.manager" && supportFragmentManager.fragments[0].tag == "com.bumptech.glide.manager") {
                finish()
            } else {
                finish()
            }
        }
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        doFragmentTransaction(supportFragmentManager, R.id.flFragContainer, frag, fragmentTag)
    }

    override fun onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }

    private fun callUserLoginDialog() {
        val userLoginDialog = UserLoginDialog()
        userLoginDialog.showUserLoginDialog(this as AppCompatActivity, object :
            LoginButtonClickInterface {
            override fun loginButtonClick() {
                performTransaction(WelcomeFragment.newInstance(false), WelcomeFragment.TAG)
            }
        })
    }
}
