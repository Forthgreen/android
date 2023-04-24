package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.UpdateUserFollowStatusInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 03-05-2021
 */
open class UpdateUserFollowStatusViewModel(application: Application) : PostActionsViewModel(application) {

    companion object {
        private const val TAG = "UpdateUserFollowStatus"
    }

    // Variables
    private val mFollowUserInetractor by lazy {
        UpdateUserFollowStatusInteractor()
    }
    private val mOnStatusUpdated = MutableLiveData<Boolean>()
    private val mOnBlockStatusUpdated = MutableLiveData<Boolean>()

    fun updateUserFollowStatus(isShowLoading: Boolean, status: Boolean, userRef: String) {
        if (isShowLoading) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mFollowUserInetractor.updateFollowStatus(RetrofitUtils.createJsonRequestBody(
                "followingRef" to userRef,
                "follow" to status
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoCommon = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoCommon.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // Request successful
                                mOnStatusUpdated.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mOnStatusUpdated.value = false
                                // Backend error
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mOnStatusUpdated.value = false
                        // Show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    isShowLoader.value = false
                    mOnStatusUpdated.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowLoader.value = false
                mOnStatusUpdated.value = false
                // Show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun updateUserBlockStatus(isShowLoading: Boolean, status: Boolean, userRef: String) {
        if (isShowLoading) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mFollowUserInetractor.updateBlockStatus(RetrofitUtils.createJsonRequestBody(
            "blockingRef" to userRef,
            "block" to status
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoCommon = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoCommon.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // Request successful
                                mOnBlockStatusUpdated.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mOnBlockStatusUpdated.value = false
                                // Backend error
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mOnBlockStatusUpdated.value = false
                        // Show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    isShowLoader.value = false
                    mOnBlockStatusUpdated.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowLoader.value = false
                mOnBlockStatusUpdated.value = false
                // Show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    // Live data to be observed in fragment
    fun onFollowStatusUpdated(): LiveData<Boolean> = mOnStatusUpdated
    fun onBlockStatusUpdated(): LiveData<Boolean> = mOnBlockStatusUpdated
}