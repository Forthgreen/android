package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.FollowListInteractor
import com.forthgreen.app.repository.models.PojoFollowList
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 22-04-2021
 */
class FollowListViewModel(application: Application) : UpdateUserFollowStatusViewModel(application) {

    companion object {
        private const val TAG = "FollowListViewModel"
    }

    // Variables
    private val mFollowListInteractor by lazy { FollowListInteractor() }
    private var mOnFollowListFetched = MutableLiveData<PojoFollowList>()

    fun getFollowList(isShowLoading: Boolean, mPage: Int, mResultSize: Int, mFollowingList: Boolean, mUserId: String = "") {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
            add("page " to mPage)
            add("limit" to mResultSize)
            add("isFollowing" to mFollowingList)
            if (mUserId.isNotEmpty())
                add("userId" to mUserId)
        }
        mCompositeDisposable.add(mFollowListInteractor.fetchFollowList(RetrofitUtils.createJsonRequestBody(
                *(requestBodyArgs.toTypedArray())
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoFollowList = response.body() as PojoFollowList
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoFollowList.code)
                        when {
                            // When Request is successful
                            pojoBackendResponse.isSuccess -> {
                                mOnFollowListFetched.value = pojoFollowList
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // Backend error
                                showMessage.postValue(PojoMessage(message = pojoFollowList.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
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

    // Live data to be observed in fragment
    fun onFollowListFetched(): LiveData<PojoFollowList> = mOnFollowListFetched
}