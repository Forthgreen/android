package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ProfileDetailsInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoProfileDetails
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 30-04-2021
 */
class ProfileDetailsViewModel(application: Application) : UpdateUserFollowStatusViewModel(application) {

    companion object {
        private const val TAG = "ProfileDetailsViewModel"
    }

    // Interactor to hit required API
    private val mProfileDetailsInteractor by lazy { ProfileDetailsInteractor() }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mFetchedProfileDetails = MutableLiveData<PojoProfileDetails>()

    fun fetchUserDetails(mShowLoader: Boolean, mUserRef: String = "", mPage: Int, mResultSize: Int) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }
        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
            add("page" to mPage)
            add("limit" to mResultSize)
            if (mUserRef.isNotEmpty()) {
                add("userRef" to mUserRef)
            }
        }
        mCompositeDisposable.add(mProfileDetailsInteractor.fetchUserDetails(RetrofitUtils.createJsonRequestBody(
                *(requestBodyArgs.toTypedArray())
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoProfileDetails = response.body() as PojoProfileDetails
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoProfileDetails.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                // Request successful
                                mFetchedProfileDetails.value = pojoProfileDetails
                                if (pojoProfileDetails.data._id == getUserProfileDataFromSharedPrefs()._id) {
                                    saveUserDetails(pojoProfileDetails.data)
                                }
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // Backend error
                                showMessage.postValue(PojoMessage(message = pojoProfileDetails.message))
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

    // Live Data to be observed in fragment
    fun onFetchedProfileDetails(): LiveData<PojoProfileDetails> = mFetchedProfileDetails

    // Saves user details to Shared Pref
    private fun saveUserDetails(user: UserProfile) {
        mUserPrefsManager.updateUserProfile(user)
    }
}