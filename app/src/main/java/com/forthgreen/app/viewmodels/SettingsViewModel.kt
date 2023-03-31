package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.DeleteAccountInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import retrofit2.Response

/**
 * @author shraychona@gmail.com
 * @since 21 May 2020
 *
 * Application parameter is passed in order to subscribe to the android life cycle
 */
class SettingsViewModel(application: Application) : AccountUpdateViewModel(application) {

    fun getUserProfile(): UserProfile? {
        return mUserPrefsManager.userProfile
    }

}