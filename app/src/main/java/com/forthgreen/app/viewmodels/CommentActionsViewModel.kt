package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.CommentActionsInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 01-05-2021
 */
open class CommentActionsViewModel(application: Application) : TagViewModel(application) {

    companion object {
        private const val TAG = "CommentActionsViewModel"
    }

    // Interactor to hit the required API with the required body
    private val mCommentActionsInteractor by lazy {
        CommentActionsInteractor()
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mOnCommentLikeSuccess = MutableLiveData<Boolean>()
    private val mOnCommentDeleted = MutableLiveData<Boolean>()
    private val mOnCommentReported = MutableLiveData<Boolean>()

    // Send Required body as API
    fun updateCommentLike(mShowLoader: Boolean, commentRef: String, liked: Boolean) {
        if (mShowLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mCommentActionsInteractor.updateCommentLike(RetrofitUtils.createJsonRequestBody(
                "like" to liked,
                "commentRef" to commentRef
        ), object : NetworkRequestCallbacks {
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
                                mOnCommentLikeSuccess.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mOnCommentLikeSuccess.value = false
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mOnCommentLikeSuccess.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    isShowLoader.value = false
                    mOnCommentLikeSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                mOnCommentLikeSuccess.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Send Required body as API
    fun deleteComment(mShowLoader: Boolean, commentRef: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mCommentActionsInteractor.deleteComment(RetrofitUtils.createJsonRequestBody(
                "commentRef" to commentRef
        ), object : NetworkRequestCallbacks {
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
                                mOnCommentDeleted.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mOnCommentDeleted.value = false
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mOnCommentDeleted.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    isShowLoader.value = false
                    mOnCommentDeleted.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                mOnCommentDeleted.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Send Required body as API
    fun reportComment(mShowLoader: Boolean, commentRef: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mCommentActionsInteractor.reportComment(RetrofitUtils.createJsonRequestBody(
                "commentRef" to commentRef
        ), object : NetworkRequestCallbacks {
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
                                mOnCommentReported.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mOnCommentReported.value = false
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mOnCommentReported.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    isShowLoader.value = false
                    mOnCommentReported.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                mOnCommentReported.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Live data to be observed in fragment
    fun onCommentLikeUpdated(): LiveData<Boolean> = mOnCommentLikeSuccess
    fun onCommentDeleted(): LiveData<Boolean> = mOnCommentDeleted
    fun onCommentReported(): LiveData<Boolean> = mOnCommentReported
}