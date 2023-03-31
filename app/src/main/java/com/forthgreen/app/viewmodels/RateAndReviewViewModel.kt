package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.RateAndReviewInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoMyReviews
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class RateAndReviewViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "RateAndReviewViewModel"
    }

    private var reviews = MutableLiveData<PojoMyReviews>()
    private val mRateAndReviewInteractor: RateAndReviewInteractor by lazy { RateAndReviewInteractor() }

    fun getMyReviews(isShowLoader: Boolean, mPage: Int, resultSize: Int) {
        if (isShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(mRateAndReviewInteractor.getMyReviews(RetrofitUtils.createJsonRequestBody(
                "page" to mPage,
                "limit" to resultSize
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoMyReviews = response.body() as PojoMyReviews
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoMyReviews.code)
                        when {
                            //request successful
                            pojoBackendResponse.isSuccess -> {
                                reviews.value = pojoMyReviews
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoMyReviews.message))
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
                    isShowSwipeRefreshLayout.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowSwipeRefreshLayout.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    //live data to observe back in fragment
    fun onSuccess(): LiveData<PojoMyReviews> = reviews
}
