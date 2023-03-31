package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.TagInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoTag
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

open class TagViewModel(application: Application) : NotificationsViewModel(application) {

    companion object {
        private const val TAG = "TagViewModel"
    }

    // Variables
    private val mTagInteractor by lazy {
        TagInteractor()
    }

    private val mOnTagsListSuccess = MutableLiveData<PojoTag>()

    fun getUsersToTag(isShowLoading: Boolean, mText: String) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(
                mTagInteractor.getUsersToTag(
                        RetrofitUtils.createJsonRequestBody(
                                "text" to mText,
                                "limit" to Integer.MAX_VALUE,
                        ), object : NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowSwipeRefreshLayout.value = false
                            val pojoNetworkRequest =
                                    RetrofitRequest.checkForNetworkResponseCode(response.code())
                            if (pojoNetworkRequest.isSuccess && response.body() != null) {
                                val pojoTag = response.body() as PojoTag
                                val pojoBackendResponse =
                                        RetrofitRequest.checkForBackendResponseCode(pojoTag.code)
                                when {
                                    pojoBackendResponse.isSuccess -> {
                                        //When the request is successful.
                                        mOnTagsListSuccess.value = pojoTag
                                    }
                                    pojoBackendResponse.isSessionExpired -> {
                                        //If the session is expired.
                                        mUserPrefsManager.clearUserPrefs()
                                        isSessionExpired.value = true
                                    }
                                    else -> {
                                        //If any other error occurs which results in the backend response not being success.
                                        showMessage.postValue(PojoMessage(message = pojoTag.message))
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

    fun onTagsListSuccess(): LiveData<PojoTag> = mOnTagsListSuccess
}