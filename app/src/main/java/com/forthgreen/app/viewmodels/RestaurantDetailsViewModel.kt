package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.RestaurantDetailsInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoRestaurantDetails
import com.forthgreen.app.repository.models.PojoRestaurantReviews
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.ApplicationGlobal
import retrofit2.Response

open class RestaurantDetailsViewModel(application: Application) : AddBookmarkViewModel(application) {

    companion object {
        const val TAG = "RestaurantDetailsViewModel"
    }

    private var receivedDetails = MutableLiveData<PojoRestaurantDetails>()
    private var receivedReviews = MutableLiveData<PojoRestaurantReviews>()
    private var statusUpdateSuccess = MutableLiveData<PojoCommon>()

    //interactor to hit required API
    private val mRestaurantDetailsInteractor by lazy { RestaurantDetailsInteractor() }

    fun getRestaurantDetails(isShowLoading: Boolean, restaurantId: String) {
        if (isShowLoading) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mRestaurantDetailsInteractor.fetchRestaurantDetails(RetrofitUtils.createJsonRequestBody(
                "restaurantId" to restaurantId,
                "latitude" to ApplicationGlobal.coordinates[0],
                "longitude" to ApplicationGlobal.coordinates[1],
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoRestaurantDetails = response.body() as PojoRestaurantDetails
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoRestaurantDetails.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //request successful
                                receivedDetails.value = pojoRestaurantDetails
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoRestaurantDetails.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowLoader.value = false
                //show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun getRestaurantReviews(isShowLoading: Boolean, page: Int, resultSize: Int, restaurantId: String) {
        if (isShowLoading) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mRestaurantDetailsInteractor.fetchRestaurantReviews(RetrofitUtils.createJsonRequestBody(
                "page" to page,
                "limit" to resultSize,
                "restaurantRef" to restaurantId
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoRestaurantReviews = response.body() as PojoRestaurantReviews
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoRestaurantReviews.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //request successful
                                receivedReviews.value = pojoRestaurantReviews
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoRestaurantReviews.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowLoader.value = false
                //show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun updateFollowStatus(isShowLoading: Boolean, status: Boolean, restaurantRef: String) {
        if (isShowLoading) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mRestaurantDetailsInteractor.followRestaurant(RetrofitUtils.createJsonRequestBody(
                "restaurantRef" to restaurantRef,
                "status" to status
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
                                //request successful
                                statusUpdateSuccess.value = pojoCommon
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowLoader.value = false
                //show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    //Return User Id of the current user.
    fun fetchUserId(): String {
        val user = mUserPrefsManager.userProfile
        return user?._id ?: ""
    }

    //func to observe data in fragment
    fun onReceivedDetails(): LiveData<PojoRestaurantDetails> = receivedDetails
    fun onReceivedReviews(): LiveData<PojoRestaurantReviews> = receivedReviews
    fun onFollowStatusUpdated(): LiveData<PojoCommon> = statusUpdateSuccess
}