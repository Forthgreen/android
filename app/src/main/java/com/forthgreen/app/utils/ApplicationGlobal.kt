package com.forthgreen.app.utils

import android.app.Application
import android.os.Environment
import com.forthgreen.app.repository.preferences.UserPrefsManager
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.MediaSourceBuilder
import im.ene.toro.exoplayer.ToroExo
import io.branch.referral.Branch
import java.io.File


/**
 * Created by ShrayChona on 03-10-2018.
 */
class ApplicationGlobal : Application() {

    companion object {
        var accessToken: String = ""
        var deviceLocale: Int = 2
        var isLoggedIn: Int = ValueMapping.getUserAccessLoggedOut()
        var showOnBoardings: Boolean = true     // Toggle to not show onboardings when logging out.
        var coordinates: Array<Double> = arrayOf(0.0, 0.0)
        var showNotificationDot: Boolean = true

        var config: Config? = null
        private var exoCreator: ExoCreator? = null
        private const val cacheFile = (150 * 1024 * 1024).toLong()

        var muteVideo: Boolean = true
    }

    override fun onCreate() {
        super.onCreate()

        // get device locale
//        deviceLocale = Locale.getDefault().language

        // Branch logging for debugging
        Branch.enableLogging()

        // Branch object initialization
        Branch.getAutoInstance(this)

        // get session id
        accessToken = UserPrefsManager(this).accessToken
//        accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImlkIjoiNWViOTJjZDljOTI4OTkwMDExNjMyMjI4IiwiZW1haWwiOiJnYXVyYXYuc2hhcm1hQGFwcC1rbml0LmNvbSIsInRva2VuTGlmZSI6IjdkIiwicm9sZSI6InVzZXIifSwiaWF0IjoxNTkwNzc1OTU3LCJleHAiOjE1OTEzODA3NTd9.cWcCFi72ZMj8vUtxorpK4_9x6jTlGsytai27E3Kv18s"
        isLoggedIn = UserPrefsManager(this).isLoggedIn

        muteVideo = UserPrefsManager(this).muteVideo

        showNotificationDot = UserPrefsManager(this).showNotifyDot

        // Video Player looping
        val PATH: String =
            Environment.getExternalStorageDirectory().toString() + "/" + "Forthgreen" + "/"
        val folder = File(PATH)
        if (!folder.exists()) {
            folder.mkdir() //If there is no folder it will be created.
        }

        val cache = SimpleCache(File(getCacheDir(), "/toro_cache"),
            LeastRecentlyUsedCacheEvictor(cacheFile))

        config = Config.Builder().setMediaSourceBuilder(MediaSourceBuilder.LOOPING)
            .setCache(cache)
            .build() //this is use for lopping

        exoCreator = ToroExo.with(this).getCreator(config)
    }
}