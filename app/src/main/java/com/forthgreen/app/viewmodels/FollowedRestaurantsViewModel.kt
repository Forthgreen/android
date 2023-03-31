package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.FollowedRestaurantsInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoRestaurantsList
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.ApplicationGlobal
import retrofit2.Response

class FollowedRestaurantsViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "FollowedRestaurantsViewModel"
    }

    //interactor to hit required API
    private val mFollowedRestaurantsInteractor by lazy { FollowedRestaurantsInteractor() }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mFetchedRestaurantsList = MutableLiveData<PojoRestaurantsList>()

    fun fetchFollowedRestaurants(
            isShowLoading: Boolean,
            mPage: Int,
            mResultSize: Int,
            refType: Int,
            mLatitude: Double,
            mLongitude: Double,
    ) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(
                mFollowedRestaurantsInteractor.getFollowedRestaurants(RetrofitUtils.createJsonRequestBody(
                        "refType" to refType,
                        "page" to mPage,
                        "limit" to mResultSize,
                        "latitude" to mLatitude,
                        "longitude" to mLongitude
                ), object : NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowSwipeRefreshLayout.value = false
                            val pojoNetworkRequest =
                                    RetrofitRequest.checkForNetworkResponseCode(response.code())
                            if (pojoNetworkRequest.isSuccess && response.body() != null) {
                                val pojoRestaurantsList = response.body() as PojoRestaurantsList
                                val pojoBackendResponse =
                                        RetrofitRequest.checkForBackendResponseCode(pojoRestaurantsList.code)
                                when {
                                    pojoBackendResponse.isSuccess -> {
                                        //request successful
                                        mFetchedRestaurantsList.value = pojoRestaurantsList
                                    }
                                    pojoBackendResponse.isSessionExpired -> {
                                        //If the session is expired.
                                        mUserPrefsManager.clearUserPrefs()
                                        isSessionExpired.value = true
                                    }
                                    else -> {
                                        //backend error
                                        showMessage.postValue(PojoMessage(message = pojoRestaurantsList.message))
                                    }
                                }
                            } else if (pojoNetworkRequest.isSessionExpired) {
                                // If session expired
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            } else {
                                //show networkRequest error
                                showMessage.postValue(
                                        PojoMessage(
                                                message = RetrofitRequest.getErrorMessage(
                                                        response.errorBody()!!
                                                )
                                        )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                        }
                    }

                    override fun onError(t: Throwable) {
                        isShowSwipeRefreshLayout.value = false
                        //show retrofit error
                        showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                    }

                })
        )
    }

    //func to observe data in fragment
    fun onFetchedRestaurantsList(): LiveData<PojoRestaurantsList> = mFetchedRestaurantsList

    fun saveUserLocation(mLatitude: Double, mLongitude: Double) {
        ApplicationGlobal.coordinates = arrayOf(mLatitude, mLongitude)
    }
}