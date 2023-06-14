package com.forthgreen.app.utils

/**
 * Created by ShrayChona on 15-12-2018.
 */
object ValueMapping {

    // For user login status
    fun getUserAccessLoggedOut() = 1
    fun getUserAccessLoggedIn() = 2
    fun getUserAccessGuest() = 3

    //For Image Mapping
    fun getPathSmall() = "small"
    fun getPathAverage() = "average"
    fun getPathBest() = "best"

    // For gender type
    fun getTypeMale() = 1
    fun getTypeFemale() = 2
    fun getTypeOther() = 3

    //For Social Login Type
    fun onSocialLoginFacebook() = 1
    fun onSocialLoginGoogle() = 2

    //Report or Review Type
    fun onBrandReport() = 1
    fun onReviewReport() = 2
    fun onRestaurantReviewReport() = 3

    // Profile Report Type
    fun onNonVeganReport() = 1
    fun onPretendReport() = 2
    fun onContentReport() = 3
    fun onOtherReport() = 4

    //For Static Data Type
    fun onStaticDataAboutUs() = 22
    fun onStaticDataTerms() = 23
    fun onStaticDataPrivacyPolicy() = 24

    // For Deep linking
    fun deepLinkingTypeBrand() = "1"
    fun deepLinkingTypeProduct() = "2"
    fun deepLinkingTypeRestaurant() = "3"
    fun deepLinkingTypeProfile() = "4"

    // For Home Feed Types
    fun onPostWithImageOnly() = 1
    fun onPostWithTextOnly() = 2
    fun onPostWithImageNText() = 3
    fun onTypeUser() = 4
    fun onPostWithVideoOnly() = 5
    fun onPostWithVideoNText() = 6

    // For Click Types on posts, comments, replies
    fun onMenuClick() = 1
    fun onLikeOrDislike() = 2
    fun onCommentsOrRepliesClick() = 3
    fun onProfileClick() = 4
    fun onLikesClick() = 5

    // For notifications Type
    fun onNotifComment() = 1
    fun onNotifReply() = 2
    fun onNotifPostLiked() = 3
    fun onNotifCommentLiked() = 4
    fun onNotifFollowingType() = 5
    fun onNotifReplyLiked() = 6
    fun onNotifTaggedComment() = 7
    fun onNotifTaggedPost() = 8
    fun onNotifAddPost() = 9

    // For Followers and following clicks
    fun onFollowersClick() = 1
    fun onFollowingClick() = 2

    // For Menu click types
    fun onPostMenuClick() = 1
    fun onCommentMenuClick() = 2
    fun onReplyMenuClick() = 3

    // For likes listing
    fun getPostLikedUsers() = 1
    fun getCommentOrRepliesLikedUsers() = 2

    // For sort options
    fun onNewestFirstSort() = 1
    fun onPriceLowToHighSort() = 2
    fun onPriceHighToLowSort() = 3

    // For categories type
    fun onClothingCategorySelected() = 1
    fun onBeautyCategorySelected() = 2
    fun onHealthCategorySelected() = 3
    fun onFoodCategorySelected() = 4
    fun onDrinksCategorySelected() = 5
    fun onMiscCategorySelected() = 6
    fun onAccessoriesCategorySelected() = 7

    // For Gender
    fun onMaleFilters() = 1
    fun onWomenFilters() = 2
    fun onBothGenderFilters() = 3

    // For Bookmarks
    fun onProductBookmarkAction() = 1
    fun onBrandBookmarkAction() = 2
    fun onRestaurantBookmarkAction() = 3

    // For Tag users, brands and restaurants
    fun onUserTaggedAction() = 1
    fun onBrandTaggedAction() = 2
    fun onRestaurantTaggedAction() = 3

    // For video thumbnail
    fun onCaptureVideoType() = 1
    fun onGalleryVideoType() = 2

}