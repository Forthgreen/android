package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.HomeFeedInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoPostsList
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 21-04-2021
 */
class HomeFeedViewModel(application: Application) : UpdateUserFollowStatusViewModel(application) {

    companion object {
        private const val TAG = "HomeFeedViewModel"
    }

    // Variables
    private val mHomeFeedInteractor by lazy {
        HomeFeedInteractor()
    }
    private var mFetchedData = MutableLiveData<PojoPostsList>()

    // Send params as body
    fun fetchFeedData(mShowLoader: Boolean, mPage: Int, mResultSize: Int) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }

        // Ignore Limit for now to use default limit from backend.
        mCompositeDisposable.add(mHomeFeedInteractor.getFeedData(RetrofitUtils.createJsonRequestBody(
                "page" to mPage,
                //"limit" to mResultSize
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoPostsList = response.body() as PojoPostsList
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoPostsList.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // when Request is successful
                                mFetchedData.value = pojoPostsList
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // Backend error
                                showMessage.postValue(PojoMessage(message = pojoPostsList.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        // Show network Request error
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
                // Show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    // Live Data to observe data in fragment
    fun onSuccessFulDataFetched(): LiveData<PojoPostsList> = mFetchedData

    fun updateNotifStatus(status: Boolean) {
        mUserPrefsManager.updateDotStatus(status)
    }

//    fun saveUserDetailsForNotifDot(user: UserProfile) {
//        mUserPrefsManager.updateUserProfile(user)
//    }

}