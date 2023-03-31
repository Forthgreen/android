package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ProductReviewInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class ProductReviewViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "ProductReviewViewModel"
    }

    //variables
    private var reviewAdded = MutableLiveData<Boolean>()
    private val mReviewInteractor: ProductReviewInteractor by lazy {
        ProductReviewInteractor()
    }

    fun addReview(productRef: String, rating: Float, title: String, review: String) {
        isShowLoader.value = true

        mCompositeDisposable.add(mReviewInteractor.addReview(RetrofitUtils.createJsonRequestBody(
                "productRef" to productRef,
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
                                reviewAdded.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                reviewAdded.value = false
                                showMessage.postValue(PojoMessage(message = pojoAddReview.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //response error
                        reviewAdded.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //any other errors
                    isShowLoader.value = false
                    reviewAdded.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //retrofit error
                reviewAdded.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun onSuccessfulAdd(): LiveData<Boolean> = reviewAdded
}