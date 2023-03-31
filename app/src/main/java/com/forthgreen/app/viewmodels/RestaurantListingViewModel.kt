package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.RestaurantListingInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoRestaurantListing
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.ApplicationGlobal
import retrofit2.Response

/**
 * Created by Chetan Tuteja on 23-Oct-20.
 * @param application is passed in order to subscribe to the android life cycle.
 */
class RestaurantListingViewModel(application: Application) : RestaurantDetailsViewModel(application) {

    companion object {
        private const val TAG = "RestaurantListingViewMo"
    }

    //Interactor to hit the required API with the required body
    private val mRestaurantListingInteractor by lazy {
        RestaurantListingInteractor()
    }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mRestaurantsFetched = MutableLiveData<PojoRestaurantListing>()
    private val mRestaurantsForMapFetched = MutableLiveData<PojoRestaurantListing>()

    //Send Required body as API
    fun fetchRestaurants(
            mShowLoader: Boolean, mPage: Int, mResultSize: Int, mLatitude: Double,
            mLongitude: Double, categoryIds: Array<Int>, mText: String = "",
    ) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }

        mCompositeDisposable.add(mRestaurantListingInteractor.fetchRestaurants(RetrofitUtils.createJsonRequestBody(
                "page" to mPage,
                "limit" to mResultSize,
                "latitude" to mLatitude,
                "longitude" to mLongitude,
                "text" to mText
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoRestaurants = response.body() as PojoRestaurantListing
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoRestaurants.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                mRestaurantsFetched.value = pojoRestaurants
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoRestaurants.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(
                                PojoMessage(
                                        message = RetrofitRequest.getErrorMessage(
                                                response.errorBody()!!
                                        )
                                )
                        )
                    }
                } catch (e: Exception) {
                    //If some error of any kind occurs while assignment or modification of values.
                    isShowSwipeRefreshLayout.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //If there is some error parsing the request itself in Interactor
                isShowSwipeRefreshLayout.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        })
        )
    }

    fun fetchRestaurantsForMap(
            mShowLoader: Boolean, mPage: Int, mResultSize: Int,
            mLatitude: Double, mLongitude: Double,
    ) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }

        mCompositeDisposable.add(
                mRestaurantListingInteractor.fetchRestaurantsForMap(RetrofitUtils.createJsonRequestBody(
                        "latitude" to mLatitude,
                        "longitude" to mLongitude,
                        "page" to mPage,
                        "limit" to mResultSize
                ), object : NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowSwipeRefreshLayout.value = false
                            val pojoNetworkRequest =
                                    RetrofitRequest.checkForNetworkResponseCode(response.code())
                            if (pojoNetworkRequest.isSuccess && response.body() != null) {
                                val pojoRestaurantsForMap = response.body() as PojoRestaurantListing
                                val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(
                                        pojoRestaurantsForMap.code
                                )
                                when {
                                    pojoBackendResponse.isSuccess -> {
                                        //When the request is successful.
                                        mRestaurantsForMapFetched.value = pojoRestaurantsForMap
                                    }
                                    pojoBackendResponse.isSessionExpired -> {
                                        //If the session is expired.
                                        mUserPrefsManager.clearUserPrefs()
                                        isSessionExpired.value = true
                                    }
                                    else -> {
                                        //If any other error occurs which results in the backend response not being success.
                                        showMessage.postValue(PojoMessage(message = pojoRestaurantsForMap.message))
                                    }
                                }
                            } else if (pojoNetworkRequest.isSessionExpired) {
                                // If session expired
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            } else {
                                //If some error occurs not handled by Backend and results in network request not being success.
                                showMessage.postValue(
                                        PojoMessage(
                                                message = RetrofitRequest.getErrorMessage(
                                                        response.errorBody()!!
                                                )
                                        )
                                )
                            }
                        } catch (e: Exception) {
                            //If some error of any kind occurs while assignment or modification of values.
                            isShowSwipeRefreshLayout.value = false
                            e.printStackTrace()
                            showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                        }
                    }

                    override fun onError(t: Throwable) {
                        //If there is some error parsing the request itself in Interactor
                        isShowSwipeRefreshLayout.value = false
                        t.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                    }
                })
        )
    }

    //LiveData to observe in the Fragment using ViewModel
    fun onRestaurantsFetched(): LiveData<PojoRestaurantListing> = mRestaurantsFetched
    fun onRestaurantsForMapFetched(): LiveData<PojoRestaurantListing> = mRestaurantsForMapFetched

    fun saveUserLocation(mLatitude: Double, mLongitude: Double) {
        ApplicationGlobal.coordinates = arrayOf(mLatitude, mLongitude)
    }
}