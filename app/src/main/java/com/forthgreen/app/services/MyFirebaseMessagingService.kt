package com.forthgreen.app.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.PushNotificationPayload
import com.forthgreen.app.repository.preferences.UserPrefsManager
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.HomeActivity
import com.forthgreen.app.views.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.util.*

/**
 * Created by ShrayChona on 03-01-2019.
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
        const val INTENT_EXTRAS_NOTIFICATION_TYPE = "notificationType"
        const val INTENT_EXTRAS_PAYLOAD = "INTENT_EXTRAS_PAYLOAD"
        const val LOCAL_INTENT_NEW_PUSH_NOTIFICATION = "LOCAL_INTENT_NEW_PUSH_NOTIFICATION"
        const val LOCAL_INTENT_PUSH_RECEIVED_SHOW_DOT = "LOCAL_INTENT_PUSH_RECEIVED_SHOW_DOT"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data: Map<String, String>? = remoteMessage!!.data
        if (null != data && data.isNotEmpty()) {
            Log.d("MyFirebaseMessaging", data.toString())
            Log.d("MyFirebaseMessaging1",  remoteMessage!!.data.toString())
            if (data.containsKey("title") && data.containsKey("reference") && data.containsKey("type")) {
                val title = data["title"]
                var message = data["body"]
                val refId = data["reference"]
                val type = data["type"]
                val payload = data["payload"]
                if (message == null) message = ""
                if (!title.isNullOrEmpty() && !refId.isNullOrEmpty() && refId!!.length > 7 && !type.isNullOrEmpty())
                    constructNotification(title!!, message!!, Integer.parseInt(refId.substring(refId.length - 7, refId.length), 16), type!!.toInt(), payload)
            }
        }
    }

    private fun constructNotification(title: String, message: String, notificationId: Int, type: Int, payload: String?) {
        if (ApplicationGlobal.accessToken.isNotEmpty()) {
            ApplicationGlobal.showNotificationDot = true
            UserPrefsManager(this).updateDotStatus(true)
            // Send Broadcast to show dot
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(
                LOCAL_INTENT_PUSH_RECEIVED_SHOW_DOT))
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(INTENT_EXTRAS_NOTIFICATION_TYPE, type)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            when (type) {
                ValueMapping.onNotifPostLiked(), ValueMapping.onNotifComment(),
                ValueMapping.onNotifReply(), ValueMapping.onNotifCommentLiked(),
                ValueMapping.onNotifReplyLiked(), ValueMapping.onNotifFollowingType(),
                ValueMapping.onNotifTaggedComment(), ValueMapping.onNotifTaggedPost(),
                -> {
                    intent.putExtra(INTENT_EXTRAS_PAYLOAD, Gson().fromJson(payload,
                            PushNotificationPayload::class.java))
                    sendNotification(notificationId, title, message, intent)

                    LocalBroadcastManager.getInstance(this).sendBroadcast(
                            Intent(LOCAL_INTENT_NEW_PUSH_NOTIFICATION)
                    )
                }
                else -> {
                    sendNotification(notificationId, title, message, intent)
                }
            }
        }
    }

    private fun sendNotification(notificationId: Int, title: String, message: String, intent: Intent) {
        val notificationManager = this
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var intentFlagType = PendingIntent.FLAG_ONE_SHOT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intentFlagType =
                PendingIntent.FLAG_IMMUTABLE // or only use FLAG_MUTABLE >> if it needs to be used with inline replies or bubbles.
        }

        val contentIntent =
            PendingIntent.getActivities(this, notificationId, arrayOf(intent), intentFlagType)

      //  notificationManager.notify(notificationId, getNotification(title, message, PendingIntent.getActivity(this, notificationId, intent,
                      //  PendingIntent.FLAG_UPDATE_CURRENT)))
        notificationManager.notify(notificationId, getNotification(title, message, contentIntent))
    }


    private fun sendGeneralNotification(title: String, message: String) {
        val notificationManager = this
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent: Intent = if (ApplicationGlobal.accessToken.isNotEmpty()) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        notificationManager.notify(0, getNotification(title, message,
                PendingIntent.getActivity(this, 0, intent, 0)))
    }

    private fun getNotification(title: String, message: String, pendingIntent: PendingIntent): Notification {
        val channelId = "mainChannelId"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "MainChannel"
            val descriptionText = "All notifications are shown here"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(
                this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setSmallIcon(getNotificationIcon())
                .setTicker(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                // .setSound(Uri.parse("android.resource://" + getPackageName() + "/" +
                //        R.raw.notification_sound))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPushNotification))
                .setColorized(true)
                .setAutoCancel(true).build()
    }

    private fun getNotificationIcon(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            R.mipmap.ic_forthgreen_notification
        else
            R.mipmap.ic_forthgreen_notification
    }
}