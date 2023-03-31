package com.forthgreen.app.repository.preferences

import android.content.Context
import android.content.SharedPreferences
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import com.google.gson.Gson

/**
 * Created by ShrayChona on 03-10-2018.
 */
class UserPrefsManager(context: Context) {

    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    companion object {
        // SharedPreference Keys
        private const val PREFS_FILENAME = "Base App"
        private const val PREFS_MODE = 0
        private const val PREFS_USER_PROFILE = "userProfile"
        private const val PREFS_REACTION_LIST = "reactionList"
        private const val PREFS_IS_LOGGED_IN_STATUS = "isLoggedInsTATUS"
        private const val PREFS_ACCESS_TOKEN = "accessToken"
        private const val PREFS_MUTE_VIDEOS = "PREFS_MUTE_VIDEOS"
        private const val PREFS_SHOW_DOT = "PREFS_SHOW_DOT"
    }

    init {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILENAME, PREFS_MODE)
        mEditor = mSharedPreferences.edit()
    }

    fun clearUserPrefs() {
        ApplicationGlobal.accessToken = ""
        ApplicationGlobal.isLoggedIn = ValueMapping.getUserAccessLoggedOut()
        mEditor.putBoolean(PREFS_SHOW_DOT, true)
        mEditor.clear()
        mEditor.apply()
    }

    val isLoggedIn: Int
        get() = mSharedPreferences.getInt(PREFS_IS_LOGGED_IN_STATUS, ValueMapping.getUserAccessGuest())

    fun saveUserSession(isRememberMe: Boolean = true, accessToken: String, userProfile: UserProfile) {
        ApplicationGlobal.accessToken = accessToken
        ApplicationGlobal.isLoggedIn = ValueMapping.getUserAccessLoggedIn()
        if (isRememberMe) {
            mEditor.putInt(PREFS_IS_LOGGED_IN_STATUS, ValueMapping.getUserAccessLoggedIn())
        }
        mEditor.putString(PREFS_ACCESS_TOKEN, accessToken)
        mEditor.putString(PREFS_USER_PROFILE, Gson().toJson(userProfile))
        mEditor.apply()
    }

    fun saveAccessToken(isRememberMe: Boolean = true, accessToken: String) {
        ApplicationGlobal.accessToken = accessToken
        if (isRememberMe) {
            mEditor.putInt(PREFS_IS_LOGGED_IN_STATUS, ValueMapping.getUserAccessLoggedIn())
        }
        mEditor.putString(PREFS_ACCESS_TOKEN, accessToken)
        mEditor.apply()
    }

    val userProfile: UserProfile?
        get() = Gson().fromJson(mSharedPreferences.getString(PREFS_USER_PROFILE, ""),
                UserProfile::class.java)

    fun updateUserProfile(userProfile: UserProfile?) {
        if (null != userProfile) {
            mEditor.putString(PREFS_USER_PROFILE, Gson().toJson(userProfile))
            mEditor.apply()
        }
    }

    fun updateLoggedInStatus(loginStatus: Int) {
        ApplicationGlobal.isLoggedIn = ValueMapping.getUserAccessGuest()
        mEditor.putInt(PREFS_IS_LOGGED_IN_STATUS, loginStatus)
        mEditor.apply()
    }

    fun updateDotStatus(notifyStatus: Boolean) {
        mEditor.putBoolean(PREFS_SHOW_DOT, notifyStatus)
        mEditor.apply()
    }

    val accessToken: String
        get() = mSharedPreferences.getString(PREFS_ACCESS_TOKEN, "")!!

    val muteVideo: Boolean
        get() = mSharedPreferences.getBoolean(PREFS_MUTE_VIDEOS, true)

    val showNotifyDot: Boolean
        get() = mSharedPreferences.getBoolean(PREFS_SHOW_DOT, true)
}