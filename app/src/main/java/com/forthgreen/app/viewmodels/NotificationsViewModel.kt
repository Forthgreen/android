package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.NotificationsInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoNotifications
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 24-04-2021
 */
open class NotificationsViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "NotificationsViewModel"
    }

    // Interactor to hit the required API with the required body
    private val mNotificationsInteractor by lazy {
        NotificationsInteractor()
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mNotificationsFetchSuccess = MutableLiveData<PojoNotifications>()
    private val mNotificationReadSuccess = MutableLiveData<Boolean>()

    // Send Description as Body to API
    fun fetchNotifications(mShowLoader: Boolean, page: Int, resultSize: Int) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(mNotificationsInteractor.fetchNotifications(RetrofitUtils.createJsonRequestBody(
                "page" to page,
                "limit" to resultSize
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoNotifications = response.body() as PojoNotifications
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoNotifications.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // When the request is successful.
                                mNotificationsFetchSuccess.value = pojoNotifications
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoNotifications.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        isShowSwipeRefreshLayout.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowSwipeRefreshLayout.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Submit Request Body
    fun markNotificationAsRead(showLoader: Boolean = false, notificationRef: String) {
        if (showLoader) {
            isShowLoader.value = true
        }

        mCompositeDisposable.add(mNotificationsInteractor.markNotifAsSeen(RetrofitUtils.createJsonRequestBody(
                "notificationId" to notificationRef
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
                                // When the request is successful.
                                mNotificationReadSuccess.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                mNotificationReadSuccess.value = false
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        // If some error occurs not handled by Backend and results in network request not being success.
                        mNotificationReadSuccess.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    isShowLoader.value = false
                    // If some error of any kind occurs while assignment or modification of values.
                    mNotificationReadSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                mNotificationReadSuccess.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // LiveData to observe in the Fragment using ViewModel
    fun onNotificationsFetched(): LiveData<PojoNotifications> = mNotificationsFetchSuccess
    fun onNotificationReadSuccess(): LiveData<Boolean> = mNotificationReadSuccess
}