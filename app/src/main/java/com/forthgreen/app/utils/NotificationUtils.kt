//package com.forthgreen.app.utils
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.ContextWrapper
//import android.content.Intent
//import android.graphics.Color
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import com.forthgreen.app.R
//import com.forthgreen.app.services.UploadVideoService
//import com.forthgreen.app.views.activities.HomeActivity
//
///**
// * Created by ShrayChona on 30-01-2019.
// */
//class NotificationUtils constructor(app: Context) : ContextWrapper(app) {
//
//    val notificationManager: NotificationManager
//        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    init {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createChannel()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createChannel() {
//        //creating channel objects with unique id
//        val videoUploadNotificationChannel = NotificationChannel(VIDEO_UPLOAD_CHANNEL_ID,
//                VIDEO_UPLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
//        videoUploadNotificationChannel.lightColor = Color.CYAN
//        videoUploadNotificationChannel.setShowBadge(true)
//        videoUploadNotificationChannel.lightColor = Color.GREEN
//        videoUploadNotificationChannel.setShowBadge(true)
//        videoUploadNotificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        videoUploadNotificationChannel.lightColor = Color.GREEN
//        notificationManager.createNotificationChannel(videoUploadNotificationChannel)
//    }
//
//    fun getNotification(): NotificationCompat.Builder {
//
//        val intent = Intent(this, UploadVideoService::class.java)
//
//        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
//        intent.putExtra(UploadVideoService.EXTRA_STARTED_FROM_NOTIFICATION, true)
//
//        // The PendingIntent that leads to a call to onStartCommand() in this foregroundService.
//        val servicePendingIntent = PendingIntent.getService(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT)
//
//
//        // The PendingIntent to launch activity.
//        val activityPendingIntent = PendingIntent.getActivity(this, 0,
//                Intent(this, HomeActivity::class.java), 0)
//
//        return NotificationCompat.Builder(this, VIDEO_UPLOAD_CHANNEL_ID)
////                .setContentText("Uploading your Video")
//                .setContentTitle(getString(R.string.video_upload_notification_text))
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.ic_logo)
////                .addAction(R.drawable.ic_cross, getString(R.string.launch_activity), activityPendingIntent)
//                .setTicker("ABCDESF")
//                .setChannelId(VIDEO_UPLOAD_CHANNEL_ID)
//                .setWhen(System.currentTimeMillis())
//    }
//
//    companion object {
//        const val VIDEO_UPLOAD_CHANNEL_ID = "videoUploadNotification"
//        const val VIDEO_UPLOAD_CHANNEL_NAME = "Video Upload Notification"
//    }
//
//}