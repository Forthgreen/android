//package com.forthgreen.app.services
//
//import android.annotation.SuppressLint
//import android.app.Service
//import android.content.Intent
//import android.net.Uri
//import android.os.Binder
//import android.os.IBinder
//import android.util.Log
//import android.widget.Toast
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.forthgreen.app.R
//import com.forthgreen.app.repository.interactors.CreatePostInteractor
//import com.forthgreen.app.repository.models.Chat
//import com.forthgreen.app.repository.models.PojoPost
//import com.forthgreen.app.repository.models.Post
//import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
//import com.forthgreen.app.repository.networkrequest.RetrofitRequest
//import com.forthgreen.app.repository.networkrequest.RetrofitUtils
//import com.forthgreen.app.utils.ApplicationGlobal
//import com.forthgreen.app.utils.GeneralFunctions
//import com.forthgreen.app.utils.ValueMapping
//import io.reactivex.disposables.CompositeDisposable
//import okhttp3.MultipartBody
//import retrofit2.Response
//import java.io.File
//import java.util.*
//
///**
// * @author shraychona@gmail.com
// * @since 20 Jul 2019
// */
//class UploadVideoService : Service() {
//    companion object {
//        private const val PACKAGE_NAME = "com.tiktik.socialapp"
//        const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.UploadVideoService"
//        private const val NOTIFICATION_ID = 12345678
//        const val INTENT_EXTRAS_VIDEO_URI = "videoUri"
//        const val INTENT_EXTRAS_CONVERSATION_ID = "conversationId"
//        const val INTENT_EXTRAS_SELF_USER_ID = "selfUserId"
//        const val INTENT_EXTRAS_POST = "post"
//    }
//
//    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
//    private val mCreatePostInteractor by lazy { CreatePostInteractor() }
//    private val videoUploadBinder: IBinder by lazy { VideoServiceBinder() }
//
//    private val mNotificationUtils by lazy { NotificationUtils(this) }
//
//    private var conversationId: String? = ""
//    private var selfUsrId: String? = ""
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return videoUploadBinder
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val videoUri = intent?.getStringExtra(INTENT_EXTRAS_VIDEO_URI)
//        conversationId = intent?.getStringExtra(INTENT_EXTRAS_CONVERSATION_ID)
//        selfUsrId = intent?.getStringExtra(INTENT_EXTRAS_SELF_USER_ID)
//        if (intent!!.hasExtra(INTENT_EXTRAS_POST)) {
//            val post = intent.getParcelableExtra<Post>(INTENT_EXTRAS_POST)
//            //starting foreground service as notification
//            if (null != post && !videoUri.isNullOrEmpty()) {
//                startForeground(NOTIFICATION_ID, mNotificationUtils.getNotification().build())
//                createPost(post, videoUri)
//            }
//        } else if (!videoUri.isNullOrEmpty() && !conversationId.isNullOrEmpty() && !selfUsrId.isNullOrEmpty()) {
//            //starting foreground service as notification
//            startForeground(NOTIFICATION_ID, mNotificationUtils.getNotification().build())
//            uploadVideo(videoUri)
//        }
//        return START_NOT_STICKY
//    }
//
//    private fun uploadVideo(videoUri: String) {
//        val storageRef by lazy { FirebaseStorage.getInstance().reference }
//        val ref = storageRef.child("msgMedia").child(GeneralFunctions.generateRandomString(12))
//        val uploadTask = ref.putFile(Uri.fromFile(File(videoUri)))
//        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
//                    this@UploadVideoService.stopSelf()
//                    throw it
//                }
//            }
//            return@Continuation ref.downloadUrl
//        }).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                sendMessage(Chat(messageType = ValueMapping.getMessageTypeVideo(), videoUrl = task.result.toString(), mediaUrl = task.result.toString(), userId = selfUsrId!!, timestamp = Timestamp(Calendar.getInstance().time)))
//            } else {
//                Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
//                this@UploadVideoService.stopSelf()
//            }
//        }
//    }
//
//    private fun sendMessage(chat: Chat) {
//        try {
//            val firebaseInstance by lazy { FirebaseFirestore.getInstance() }
//            Thread(Runnable {
//                try {
//                    firebaseInstance.collection(getString(R.string.firestore_conversation_key)).document(conversationId!!).collection(getString(R.string.firestore_msg_key)).add(chat)
//                            .addOnSuccessListener {
//                                this@UploadVideoService.stopSelf()
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
//                                this@UploadVideoService.stopSelf()
//                            }
//                } catch (e: Exception) {
//                    Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
//                    this@UploadVideoService.stopSelf()
//                    Log.e("chat failed", e.toString())
//                }
//            }).start()
//        } catch (e: Throwable) {
//            Toast.makeText(this, "Video upload failed", Toast.LENGTH_SHORT).show()
//            this@UploadVideoService.stopSelf()
//            Log.e("chat failed", e.toString())
//            //manage error
//        }
//    }
//
//    private fun createPost(post: Post, userVideoPath: String) {
//        val userVideoFile: File? = File(userVideoPath)
//        post.type = ValueMapping.getPostTypeVideo()
//        var userVideoPart: MultipartBody.Part? = null
//        if (null != userVideoFile && userVideoFile.exists()) {
//            userVideoPart = RetrofitUtils.createPartFromFile("video", userVideoFile)
//        }
//        mCompositeDisposable.add(mCreatePostInteractor.createPost(
//                RetrofitUtils.createJsonRequestBody(
//                        "postType" to post.type,
//                        "text" to post.text,
//                        "regionRef" to post.regionRef,
//                        "communityRef" to post.communityRef,
//                        "locale" to ApplicationGlobal.deviceLocale), userVideoPart, object : NetworkRequestCallbacks {
//            override fun onSuccess(response: Response<*>) {
//                try {
//                    val pojoNetworkResponse = RetrofitRequest
//                            .checkForNetworkResponseCode(response.code())
//                    if (pojoNetworkResponse.isSuccess && null != response.body()) {
//                        val pojoPost = response.body() as PojoPost
//                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoPost.code)
//                        when {
//                            pojoBackendResponse.isSuccess -> {
//                                val intent = Intent(CreatePostFragment.LOCAL_INTEND_POST_CREATED)
//                                LocalBroadcastManager.getInstance(this@UploadVideoService).sendBroadcast(intent)
//                                this@UploadVideoService.stopSelf()
//                            }
//                            pojoBackendResponse.isSessionExpired -> {
//                                this@UploadVideoService.stopSelf()
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    this@UploadVideoService.stopSelf()
//                }
//            }
//
//            override fun onError(t: Throwable) {
//                t.printStackTrace()
//                this@UploadVideoService.stopSelf()
//            }
//        }))
//    }
//
//    internal inner class VideoServiceBinder : Binder() {
//        val foregroundService: UploadVideoService
//            get() = this@UploadVideoService
//    }
//
//    override fun onUnbind(intent: Intent?): Boolean {
//        return true
//    }
//}