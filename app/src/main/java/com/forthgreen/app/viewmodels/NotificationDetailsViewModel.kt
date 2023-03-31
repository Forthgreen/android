package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.NotificationDetailsInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoNotificationDetails
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 05-05-2021
 */
class NotificationDetailsViewModel(application: Application) : PostActionsViewModel(application) {

    companion object {
        private const val TAG = "NotificationDetailsViewModel"
    }

    // Interactor to hit the required API with the required body
    private val mNotificationDetailsInteractor by lazy {
        NotificationDetailsInteractor()
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mNotificationDetailsFetchSuccess = MutableLiveData<PojoNotificationDetails>()

    // Send Description as Body to API
    fun fetchNotificationDetails(mShowLoader: Boolean, mNotificationId: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mNotificationDetailsInteractor.fetchNotificationDetails(RetrofitUtils.createJsonRequestBody(
                "notificationId" to mNotificationId
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoNotificationDetails = response.body() as PojoNotificationDetails
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoNotificationDetails.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // When the request is successful.
                                mNotificationDetailsFetchSuccess.value = pojoNotificationDetails
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            pojoBackendResponse.isInvalidApproach -> {
                                mNotificationDetailsFetchSuccess.value = pojoNotificationDetails
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoNotificationDetails.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        isShowLoader.value = false
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
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Live data to be observed in fragment
    fun onNotificationDetailsFetched(): LiveData<PojoNotificationDetails> = mNotificationDetailsFetchSuccess
}