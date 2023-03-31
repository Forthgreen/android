package com.forthgreen.app.repository.networkrequest

/**
 * Created by ShrayChona on 03-10-2018.
 */
object WebConstants {
   // private const val ACTION_BASE_URL = "http://3.15.254.192/development/"
//    private const val ACTION_BASE_URL = "http://52.14.88.170/beta/"
  //  private const val ACTION_BASE_URL = "http://3.23.147.117/staging/"

    private const val ACTION_BASE_URL = "https://profile.forthgreen.com/staging/"
   // private const val ACTION_BASE_URL = "https://forthgreen.in.ngrok.io/"
  //  private const val ACTION_BASE_URL = "https://4539-183-82-152-144.ngrok.io/"
 //   private const val ACTION_BASE_URL = "http://18.117.6.190/development/"
  //  private const val ACTION_BASE_URL = "https://92ec-136-232-143-82.ngrok.io/"
   // private const val ACTION_BASE_URL = " https://d14b-136-232-143-82.ngrok.io/"

    const val ACTION_BASE_URL_FOR_APIS = ACTION_BASE_URL + "api/"

//    const val ACTION_BASE_URL_FOR_MEDIA = "https://forthgreen.s3.us-east-2.amazonaws.com/development/images/"
//    const val ACTION_BASE_URL_FOR_MEDIA = "https://forthgreen.s3.us-east-2.amazonaws.com/beta/images/"
    const val ACTION_BASE_URL_FOR_MEDIA = "https://forthgreen.s3.us-east-2.amazonaws.com/staging/images/"

//    const val ACTION_BASE_URL_FOR_MEDIA_VIDEOS = "https://forthgreen.s3.us-east-2.amazonaws.com/development/video/"
//    const val ACTION_BASE_URL_FOR_MEDIA_VIDEOS = "https://forthgreen.s3.us-east-2.amazonaws.com/beta/video/"
    const val ACTION_BASE_URL_FOR_MEDIA_VIDEOS = "https://forthgreen.s3.us-east-2.amazonaws.com/staging/video/"

    const val EXTERNAL_LINK_FOR_TERMS_AND_CONDITIONS =
        "https://forthgreen.com/terms-and-conditions.html"
    const val EXTERNAL_LINK_FOR_PRIVACY_AND_POLICY = "https://forthgreen.com/privacy-policy.html"
    const val EXTERNAL_LINK_FOR_ABOUT_US = "https://forthgreen.com/about.html"
    const val EXTERNAL_LINK_FOR_LEAVE_FEEDBACK =
        "https://docs.google.com/forms/d/1L0_XD8zrM6YmQ2NUkFGRiwXZYX-zChlsQwlW-BxXA38/edit"

    // Date Time Format Constants
    const val DATE_FORMAT_SERVER = "yyyy-MM-ddTHH:MM:SS"
    const val DATE_FORMAT_DISPLAY = "d MMM, yyyy H:mm"

    // Temp variable
    const val IMAGE_URL = "https://picsum.photos/900/900?image=30"

    fun fetchVideoURL(key: String): String = ACTION_BASE_URL_FOR_MEDIA_VIDEOS + key

}