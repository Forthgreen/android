package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.NotificationDetailsInteractor
import com.forthgreen.app.repository.models.PojoCommentsList
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoNotificationDetails
import com.forthgreen.app.repository.models.PostDetailResponse
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 05-05-2021
 */
class NotificationPostDetailsViewModel(application: Application) : PostActionsViewModel(application) {

    companion object {
        private const val TAG = "NotificationDetailsViewModel"
    }

    // Interactor to hit the required API with the required body
    private val mNotificationDetailsInteractor by lazy {
        NotificationDetailsInteractor()
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mNotificationDetailsFetchSuccess = MutableLiveData<PostDetailResponse>()
    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mCommentsFetched = MutableLiveData<PojoCommentsList>()

    // Send Description as Body to API
    fun fetchPostDetails(mShowLoader: Boolean, mPostId: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }
        mCompositeDisposable.add(mNotificationDetailsInteractor.fetchPostDetails(RetrofitUtils.createJsonRequestBody(
                "postRef" to mPostId
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoNotificationDetails = response.body() as PostDetailResponse
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoNotificationDetails.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // When the request is successful.
                                mNotificationDetailsFetchSuccess.value = pojoNotificationDetails
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            pojoBackendResponse.isInvalidApproach -> {
                                mNotificationDetailsFetchSuccess.value = pojoNotificationDetails
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoNotificationDetails.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        isShowLoader.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Send Required body as API
    fun fetchCommentsList(mShowLoader: Boolean, page: Int, postRef: String, commentRef: String = "", resultSize: Int = 0) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }
        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
//            add("limit" to resultSize)
            add("page" to page)
            add("postRef" to postRef)
            if (commentRef.isNotEmpty()) {
                add("commentRef" to commentRef)
            }
        }
        mCompositeDisposable.add(mNotificationDetailsInteractor.fetchCommentsList(RetrofitUtils.createJsonRequestBody(
            *(requestBodyArgs.toTypedArray())
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoCommentsList = response.body() as PojoCommentsList
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoCommentsList.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // When the request is successful.
                                mCommentsFetched.value = pojoCommentsList
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommentsList.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    isShowSwipeRefreshLayout.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowSwipeRefreshLayout.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // LiveData to observe in the Fragment using ViewModel
    fun onCommentsFetched(): LiveData<PojoCommentsList> = mCommentsFetched

    // Live data to be observed in fragment
    fun onNotificationDetailsFetched(): LiveData<PostDetailResponse> = mNotificationDetailsFetchSuccess
}