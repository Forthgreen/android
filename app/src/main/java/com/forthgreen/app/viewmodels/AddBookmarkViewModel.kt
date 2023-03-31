package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.AddBookmarkInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

open class AddBookmarkViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "AddBookmarkViewModel"
    }

    // Variables
    private val mAddBookmarkInteractor by lazy {
        AddBookmarkInteractor()
    }

    private val mOnBookmarkAddSuccess = MutableLiveData<Boolean>()

    fun addBookmark(isShowLoading: Boolean, ref: String, refType: Int, status: Boolean) {
        if (isShowLoading) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(
            mAddBookmarkInteractor.addBookmark(RetrofitUtils.createJsonRequestBody(
                "ref" to ref,
                "refType" to refType,
                "status" to status
            ), object : NetworkRequestCallbacks {
                override fun onSuccess(response: Response<*>) {
                    try {
                        isShowLoader.value = false
                        val pojoNetworkRequest =
                            RetrofitRequest.checkForNetworkResponseCode(response.code())
                        if (pojoNetworkRequest.isSuccess && response.body() != null) {
                            val pojoCommon = response.body() as PojoCommon
                            val pojoBackendResponse =
                                RetrofitRequest.checkForBackendResponseCode(pojoCommon.code)
                            when {
                                pojoBackendResponse.isSuccess -> {
                                    //When the request is successful.
                                    mOnBookmarkAddSuccess.value = true
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //If any other error occurs which results in the backend response not being success.
                                    mOnBookmarkAddSuccess.value = false
                                    showMessage.postValue(PojoMessage(message = pojoCommon.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //If some error occurs not handled by Backend and results in network request not being success.
                            mOnBookmarkAddSuccess.value = false
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
                        mOnBookmarkAddSuccess.value = false
                        e.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                    }
                }

                override fun onError(t: Throwable) {
                    //If there is some error parsing the request itself in Interactor
                    mOnBookmarkAddSuccess.value = false
                    isShowLoader.value = false
                    t.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                }
            })
        )
    }


    fun onBookmarkAddSuccess(): LiveData<Boolean> = mOnBookmarkAddSuccess
}