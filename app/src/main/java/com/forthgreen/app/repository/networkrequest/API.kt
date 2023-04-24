package com.forthgreen.app.repository.networkrequest

import com.forthgreen.app.repository.models.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by ShrayChona on 03-10-2018.
 */
interface API {

    //API toSignUp
    @POST("user/signup")
    fun userSignUp(@Body body: RequestBody): Observable<Response<PojoCommon>>

    //API to SignIn
    @POST("user/login")
    fun userSignIn(@Body body: RequestBody): Observable<Response<PojoUserProfile>>

    //API to SignIn and SignUp via FB and google
    @Multipart
    @POST("user/socialLogin")
    fun userSocialLogin(
        @Part("data") body: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): Observable<Response<PojoUserProfile>>

    //API toSignUp
    @POST("user/resendVerification")
    fun forgotPassword(@Body body: RequestBody): Observable<Response<PojoCommon>>

    //API to get brand list
    @POST("brand/list")
    fun getBrandListGuest(@Body body: RequestBody): Observable<Response<PojoBrandList>>

    // API to get brand list for logged in user
    @POST("brand/listForUser")
    fun getBrandList(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody
    ): Observable<Response<PojoBrandList>>

    //API to get product List
    @POST("product/listAll")
    fun getProductListGuest(@Body body: RequestBody): Observable<Response<PojoProductList>>

    // API to get product list for login user
    @POST("product/listAllForUser")
    fun getProductList(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody
    ): Observable<Response<PojoProductList>>

    //API to get myBrands - user followed brands
    @POST("bookmark/list")
    fun getBookmarkedBrands(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoMyBrands>>

    //API to get myProducts - user followed products
    @POST("bookmark/list")
    fun getBookmarkedProducts(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoProductList>>

    //API to get details of brand
    @POST("brand/detailsForUser")
    fun getBrandDetail(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoBrandDetail>>

    //API to get details of brand for guest
    @POST("brand/detailsForGuest")
    fun getBrandDetailGuest(@Body body: RequestBody): Observable<Response<PojoBrandDetail>>

    //API to update user profile details
    @Multipart
    @POST("user/update")
    fun updateDetails(
            @Header("Authorization") accessToken: String,
            @Part("data") body: RequestBody?,
            @Part image: MultipartBody.Part?,
    ): Observable<Response<PojoUserUpdate>>

    //API to report brand or review
    @POST("report/brandOrReview")
    fun report(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody?,
    ): Observable<Response<PojoCommon>>

    //API to get myReviews List
    @POST("rateAndReview/myList")
    fun listMyReviews(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoMyReviews>>

    // API to get Product Details.
    @POST("product/details")
    fun getProductDetail(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoProductDetail>>

    // API to get Product details for Guest.
    @POST("product/detailsGuest")
    fun getGuestProductDetail(
            @Body body: RequestBody,
    ): Observable<Response<PojoProductDetail>>

    //API to add website click
    @POST("statistics/websiteClick")
    fun reportWebsiteClick(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    //API to add website click
    @POST("statistics/productVisit")
    fun reportProductVisit(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    //API to add product review
    @POST("rateAndReview/add")
    fun addReview(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    //API for Searching Brands & Products
    @POST("brand/search")
    fun searchBrandProduct(@Body body: RequestBody): Observable<Response<PojoBrandProductSearch>>

    //API for Following a Brand
    @POST("followBrand/update")
    fun followBrand(@Header("Authorization") accessToken: String, @Body body: RequestBody): Observable<Response<PojoCommon>>

    //API for Listing Static Data like T&C, Privacy Policy etc
    @POST("appDetails/list")
    fun fetchStaticData(): Observable<Response<PojoStaticData>>

    //API to fetch followed restaurants
    @POST("bookmark/list")
    fun fetchFollowedRestaurants(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoRestaurantsList>>

    //API to fetch details for restaurants
    @POST("restaurant/details")
    fun fetchRestaurantsDetails(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoRestaurantDetails>>

    //API to get details of restaurant for guest
    @POST("restaurant/detailsGuest")
    fun fetchRestaurantDetailsGuest(@Body body: RequestBody): Observable<Response<PojoRestaurantDetails>>

    //API to fetch reviews for restaurants
    @POST("rateRestaurant/list")
    fun fetchRestaurantsReviews(@Body body: RequestBody): Observable<Response<PojoRestaurantReviews>>

    //API to update follow status.
    @POST("followRestaurant/update")
    fun updateFollowStatus(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    //API to add restaurant review
    @POST("rateRestaurant/add")
    fun addRestaurantReview(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    //API for Listing Restaurants
    @POST("restaurant/list")
    fun fetchRestaurantsGuest(@Body body: RequestBody): Observable<Response<PojoRestaurantListing>>

    // API for listing restaurants for login user
    @POST("restaurant/listForUser")
    fun fetchRestaurants(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody
    ): Observable<Response<PojoRestaurantListing>>

    @POST("restaurant/map")
    fun fetchRestaurantsForMap(@Body body: RequestBody): Observable<Response<PojoRestaurantListing>>

    // API for Following Product
    @POST("followProduct/update")
    fun saveProduct(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to fetch home feed data
    @POST("post/feed")
    fun fetchFeedData(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoPostsList>>

    // API to fetch home feed data
    @POST("post/feedfollowing")
    fun fetchFeedFollowingData(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoPostsList>>

    // API to fetch feed posts for guest
    @POST("post/guestFeed")
    fun fetchFeedDataForGuest(@Body body: RequestBody): Observable<Response<PojoPostsList>>

    // API for followers and following users list
    @POST("follow/list")
    fun fetchFollowersAndFollowingUsers(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoFollowList>>

    // API for searching users
    @POST("user/search")
    fun searchUsers(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoUsersList>>

    // API for listing comments
    @POST("comment/list")
    fun fetchComments(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommentsList>>

    // API for listing notifications
    @POST("notification/list")
    fun fetchNotifications(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoNotifications>>

    // API to mark notification as seen.
    @POST("notification/seen")
    fun markNotifAsSeen(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // Fetch User Details
    @POST("user/profile")
    fun fetchUserDetails(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoProfileDetails>>

    // API to Add Comment
    @POST("comment/add")
    fun addComment(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoAddComment>>

    // API to update comment or reply likes
    @POST("comment/like")
    fun updateCommentLike(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to delete comment or replies
    @POST("comment/delete")
    fun deleteComment(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to report comment and reply
    @POST("report/comment")
    fun reportComment(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API for follow status update
    @POST("follow/user")
    fun updateUserFollowStatus(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API for user block status update
    @POST("block/user")
    fun updateUserBlockStatus(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to report user
    @POST("report/user")
    fun reportUser(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to like post
    @POST("post/like")
    fun updatePostLike(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to report post
    @POST("report/post")
    fun reportPost(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to delete post
    @POST("post/delete")
    fun deletePost(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to fetch notifications details
    @POST("notification/details")
    fun fetchNotificationDetails(
            @Header("Authorization") accessToken: String,
            @Body body: RequestBody,
    ): Observable<Response<PojoNotificationDetails>>

    // API to Create Post
    @Multipart
    @POST("post/add")
    fun createPost(
        @Header("Authorization") accessToken: String,
        @Part("data") body: RequestBody,
        @Part vararg images: MultipartBody.Part?,
        @Part video: MultipartBody.Part?,
        @Part thumbnail: MultipartBody.Part?,
    ): Single<Response<PojoCommon>>

    @POST("post/likeList")
    fun fetchLikesList(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoUsersList>>

    @POST("bookmark/add")
    fun addBookmark(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoCommon>>

    // API to get users to be tagged
    @POST("tag/list")
    fun getUsersToTag(
        @Header("Authorization") accessToken: String,
        @Body body: RequestBody,
    ): Observable<Response<PojoTag>>

    // API to show 20 products in shop for every category
    @POST("product/home")
    fun fetchProductsCategory(
        @Header("Authorization") accessToken: String
    ): Observable<Response<PojoProductCategory>>

    // API to show 20 products in shop for every category for guest user
    @POST("product/homeForAll")
    fun fetchProductsCategoryGuest(
    ): Observable<Response<PojoProductCategory>>

    // Api to delete account
    @POST("user/deleteaccount")
    fun deleteAccount(@Header("Authorization") accessToken: String) : Observable<Response<PojoCommon>>

}
