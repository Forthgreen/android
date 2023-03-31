package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.RestaurantReviewInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class RestaurantReviewViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "RestaurantReviewViewModel"
    }

    private var restaurantReviewSuccess = MutableLiveData<PojoCommon>()
    private val mReviewInteractor by lazy {
        RestaurantReviewInteractor()
    }

    fun addRestaurantReview(restaurantRef: String, rating: Float, title: String, review: String) {
        isShowLoader.value = true

        mCompositeDisposable.add(mReviewInteractor.addRestaurantReview(RetrofitUtils.createJsonRequestBody(
                "restaurantRef" to restaurantRef,
                "rating" to rating,
                "title" to title,
                "review" to review
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoAddReview = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoAddReview.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                restaurantReviewSuccess.value = pojoAddReview
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoAddReview.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //response error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //any other errors
                    isShowLoader.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //retrofit error
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    fun onSuccessfulAddedReview(): LiveData<PojoCommon> = restaurantReviewSuccess
}