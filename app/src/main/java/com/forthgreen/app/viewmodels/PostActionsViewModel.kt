package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.PostActionsInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import okhttp3.RequestBody
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 03-05-2021
 */
open class PostActionsViewModel(application: Application) : CommentActionsViewModel(application) {

    companion object {
        private const val TAG = "PostActionsViewModel"
        private const val DELETE_POST = "DELETE_POST"
        private const val REPORT_POST = "REPORT_POST"
        private const val LIKE_POST = "LIKE_POST"
    }

    // Interactor to hit the required API with the required body
    private val mPostActionsInteractor by lazy {
        PostActionsInteractor()
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mOnPostLikeSuccess = MutableLiveData<Boolean>()
    private val mOnPostDeleted = MutableLiveData<Boolean>()
    private val mOnPostReported = MutableLiveData<Boolean>()

    // Send Required body as API
    fun updatePostLike(mShowLoader: Boolean, postRef: String, liked: Boolean) {
        if (mShowLoader) {
            isShowLoader.value = true
        }

        val requestBody = RetrofitUtils.createJsonRequestBody(
                "postRef" to postRef,
                "like" to liked
        )
        performAPICall(requestBody, mOnPostLikeSuccess, LIKE_POST)
    }

    // Send Required body as API
    fun deletePost(mShowLoader: Boolean, postRef: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }

        val requestBody = RetrofitUtils.createJsonRequestBody(
                "postRef" to postRef
        )
        performAPICall(requestBody, mOnPostDeleted, DELETE_POST)
    }

    // Send Required body as API
    fun reportPost(mShowLoader: Boolean, postRef: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }

        val requestBody = RetrofitUtils.createJsonRequestBody(
                "postRef" to postRef
        )
        performAPICall(requestBody, mOnPostReported, REPORT_POST)
    }

    private fun performAPICall(body: RequestBody, mutableLiveData: MutableLiveData<Boolean>, apiToCall: String, ) {
        val networkRequestCallbacks = object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoCommon = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoCommon.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // When the request is successful.
                                mutableLiveData.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mutableLiveData.value = false
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mutableLiveData.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    isShowLoader.value = false
                    mutableLiveData.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                mutableLiveData.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }

        when (apiToCall) {
            DELETE_POST -> {
                mCompositeDisposable.add(mPostActionsInteractor.deletePost(body, networkRequestCallbacks))
            }
            REPORT_POST -> {
                mCompositeDisposable.add(mPostActionsInteractor.reportPost(body, networkRequestCallbacks))
            }
            LIKE_POST -> {
                mCompositeDisposable.add(mPostActionsInteractor.updatePostLike(body, networkRequestCallbacks))
            }
        }
    }

    // Live data to be observed in fragment
    fun onPostLikeUpdated(): LiveData<Boolean> = mOnPostLikeSuccess
    fun onPostDeleted(): LiveData<Boolean> = mOnPostDeleted
    fun onPostReported(): LiveData<Boolean> = mOnPostReported
}