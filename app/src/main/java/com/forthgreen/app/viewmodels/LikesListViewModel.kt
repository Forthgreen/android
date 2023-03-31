package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.LikesListInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoUsersList
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 09-08-2021
 */
class LikesListViewModel(application: Application) : UpdateUserFollowStatusViewModel(application) {

    companion object {
        private const val TAG = "LikesListViewModel"
    }

    // Variables
    private val mLikesInteractor by lazy { LikesListInteractor() }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mUsersFetched = MutableLiveData<PojoUsersList>()

    // Send Required body as API
    fun fetchUsers(
            isShowLoader: Boolean, mPage: Int, mResultSize: Int,
            mRef: String, mLikeType: Int,
    ) {
        if (isShowLoader)
            isShowSwipeRefreshLayout.value = true

        mCompositeDisposable.add(mLikesInteractor.fetchLikesList(RetrofitUtils.createJsonRequestBody(
                "ref" to mRef,
                "likeType" to mLikeType,
                "page" to mPage,
                "limit" to mResultSize
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoUsersList = response.body() as PojoUsersList
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoUsersList.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // When the request is successful.
                                mUsersFetched.value = pojoUsersList
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoUsersList.message))
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
                    isShowSwipeRefreshLayout.value = false
                    // If some error of any kind occurs while assignment or modification of values.
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

    // LiveData to be observed in the Fragment using ViewModel
    fun onSuccessfulUsersFetched(): LiveData<PojoUsersList> = mUsersFetched
}