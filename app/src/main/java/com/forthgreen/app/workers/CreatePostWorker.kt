package com.forthgreen.app.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.Users
import com.forthgreen.app.repository.networkrequest.RestClient
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.repository.preferences.UserPrefsManager
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.viewmodels.CreatePostViewModel
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import org.json.JSONArray
import java.io.File
import kotlin.random.Random

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 04-May-21
 */
class CreatePostWorker(private val context: Context, params: WorkerParameters) : RxWorker(context, params) {

    companion object {
        private const val TAG = "CreatePostWorker"
        private const val NOTIFICATION_CHANNEL_NAME = "Create Post Notifications"
        private const val NOTIFICATION_CHANNEL_ID = "CreatePostNotificationChannelId"
        private const val NOTIFICATION_CHANNEL_DESC = "Creates Notification for Posts"
        const val LOCAL_INTENT_UPLOAD_STATUS = "LOCAL_INTENT_UPLOAD_STATUS"
        const val BUNDLE_EXTRAS_STATUS = "BUNDLE_EXTRAS_STATUS"

        const val LOCAL_INTENT_POST_CREATED = "LOCAL_INTENT_POST_CREATED"

        @JvmStatic
        fun schedule(context: Context, workData: Data = workDataOf(), uniqueKey: String) {
            val constraints = Constraints.Builder().apply {
                setRequiredNetworkType(NetworkType.CONNECTED)
            }.build()

            val worker = OneTimeWorkRequestBuilder<CreatePostWorker>().apply {
                setConstraints(constraints)
                if (workData.keyValueMap.isNotEmpty()) {
                    setInputData(workData)
                }
                addTag(uniqueKey)
            }.build()

            WorkManager.getInstance(context)
                    .enqueueUniqueWork(uniqueKey, ExistingWorkPolicy.APPEND_OR_REPLACE,
                            worker)
        }
    }

    // Variables
    private val mUserPrefsManager: UserPrefsManager = UserPrefsManager(context)

    override fun createWork(): Single<Result> {
        val notificationId = Random.nextInt()

        // Show upload progress bar
        showUploadProgress(true)

        /* makeStatusNotification(context.getString(R.string.creating_post_notification),
                 notificationId, ongoing = true)*/

        // Extract out Input Data.
        val postText = inputData.getString(CreatePostViewModel.KEY_TEXT)
        val postImages = inputData.getStringArray(CreatePostViewModel.KEY_MEDIA)
        val tagUserList = inputData.getStringArray(CreatePostViewModel.KEY_TAGS)
        val video = inputData.getString(CreatePostViewModel.KEY_VIDEO)
        val thumbnail = inputData.getString(CreatePostViewModel.KEY_THUMBNAIL)
        val type = inputData.getInt(CreatePostViewModel.KEY_TYPE, 0)
        val width = inputData.getInt(CreatePostViewModel.KEY_WIDTH, 0)
        val height = inputData.getInt(CreatePostViewModel.KEY_HEIGHT, 0)

        var postImageList: ArrayList<MultipartBody.Part?> = arrayListOf()

        // Check out if image data is empty or not, else fetch Multipart Body of that.
        if (!postImages.isNullOrEmpty()) {
            // Map Post Image Strings to their corresponding Uris.
            val postImageUris = postImages.map { image -> Uri.parse(image) }.toTypedArray()
            postImageList = fetchMultipartImages(*(postImageUris))
        }
        var userVideoPart: MultipartBody.Part? = null
        if (!video.isNullOrEmpty()) {
            userVideoPart = RetrofitUtils.createPartFromFile("video", File(video))
        }
        var userThumbnailPart: MultipartBody.Part? = null
        if (thumbnail != null) {
            userThumbnailPart = RetrofitUtils.createPartFromFile("thumbnailImage", File(thumbnail))
        }

        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
            // Create Request Body out of non-empty and non-null data.
            if (!postText.isNullOrEmpty()) {
                add(CreatePostViewModel.KEY_TEXT to postText)
            }
            if (!tagUserList.isNullOrEmpty()) {
                val taggedData = tagUserList.map { tag -> Gson().fromJson(tag, Users::class.java) }
                add(CreatePostViewModel.KEY_TAGS to JSONArray(Gson().toJson(taggedData)))
            }
            add(CreatePostViewModel.KEY_TYPE to type)
            add(CreatePostViewModel.KEY_WIDTH to width)
            add(CreatePostViewModel.KEY_HEIGHT to height)
        }

        val requestBody = RetrofitUtils.createJsonRequestBody(*(requestBodyArgs.toTypedArray()))

        val response = RestClient.get().createPost(
            ApplicationGlobal.accessToken,
            requestBody,
            *(postImageList.toTypedArray()),
            video = userVideoPart,
            thumbnail = userThumbnailPart
        )

        return try {
            response.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { apiResponse ->
                        val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(apiResponse.code())
                        if (pojoNetworkRequest.isSuccess && apiResponse.body() != null) {
                            val pojoCommon = apiResponse.body() as PojoCommon
                            val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoCommon.code)
                            when {
                                pojoBackendResponse.isSuccess -> {
                                    // When the request is successful.
                                    /*makeStatusNotification(
                                            context.getString(R.string.create_post_success_notification),
                                            notificationId, ongoing = false)*/
                                    showUploadProgress(false)
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(
                                            Intent(LOCAL_INTENT_POST_CREATED)
                                    )
                                    Result.success()
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    // If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    Result.failure()
                                }
                                else -> {
                                    // If any other error occurs which results in the backend response not being success.
                                    Log.d(TAG, "doWork: ${pojoCommon.message}")
                                    showUploadProgress(false)
                                    makeStatusNotification(pojoCommon.message, notificationId,
                                        ongoing = false, showToast = true)
                                    Result.failure()
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            Result.failure()
                        } else {
                            // If some error occurs not handled by Backend and results in network request not being success.
                            val message = RetrofitRequest.getErrorMessage(apiResponse.errorBody()!!)
                            Log.d(TAG, "doWork: $message")
                            showUploadProgress(false)
                            makeStatusNotification(message, notificationId, ongoing = false,
                                showToast = true)
                            Result.failure()
                        }
                    }
                .doOnError { throwable ->
                    performExceptionHandling(throwable, notificationId)
                    Result.failure()
                }
        } catch (e: Exception) {
            showUploadProgress(false)
            performExceptionHandling(e, notificationId)
            Single.just(Result.failure())
        }
    }

    private fun showUploadProgress(ongoing: Boolean = false) {
        // Send progress status
        LocalBroadcastManager.getInstance(context).sendBroadcast(
            Intent(LOCAL_INTENT_UPLOAD_STATUS).apply {
                putExtra(BUNDLE_EXTRAS_STATUS, ongoing)
            })
    }

    // Check for Error Message and make corresponding Notification.
    private fun performExceptionHandling(throwable: Throwable, notificationId: Int) {
        val message = try {
            context.resources.getString(
                RetrofitRequest.getRetrofitError(throwable))
        } catch (e: Exception) {
            context.resources.getString(R.string.retrofit_failure)
        }
        Log.d(TAG, "doWork: $message")
        makeStatusNotification(message, notificationId, ongoing = false, showToast = true)
    }


    // Make Multipart Images of the specific URIs by converting them to input streams.
    private fun fetchMultipartImages(vararg itemImages: Uri): ArrayList<MultipartBody.Part?> {
        val itemList = arrayListOf<MultipartBody.Part?>()

        itemImages.forEach { uri ->
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                itemList.add(RetrofitUtils.createPartFromInputStream(
                        partName = "image${itemList.size}", inputStream))
            }
        }
        return itemList
    }


    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     *
     * For this Worker, this is used to show a notification so that you know when different steps
     * of the background work chain are starting
     *
     * @param message Message shown on the notification
     */
    private fun makeStatusNotification(
            message: String, notificationId: Int,
            ongoing: Boolean = false,
            showToast: Boolean = false,
    ) {
        if (showToast) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            return
        }
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = NOTIFICATION_CHANNEL_NAME
            val channelDescription = NOTIFICATION_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = channelDescription
            }

            // Add the channel
            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            notificationManager?.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_forthgreen_notification)
                .setContentTitle(context.resources.getString(R.string.app_name))
                .setColor(ContextCompat.getColor(context, R.color.colorPushNotification))
                .setColorized(true)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setTicker(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setOngoing(ongoing)

        // Show the notification
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
}