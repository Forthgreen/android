package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.SignInInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoUserProfile
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import retrofit2.Response

/**
 * @author shraychona@gmail.com
 * @since 18 May 2020
 *
 * Application parameter is passed in order to subscribe to the android life cycle
 */

class SignInViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "SignUpViewModel"
    }

    //Interactor to hit the required API with the required body
    private val mSignInInteractor by lazy { SignInInteractor() }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mSuccessfulSignIp = MutableLiveData<PojoUserProfile>()

    //Send details for userSignIn as body
    fun userSignIn(mEmail: String, mPassword: String) {
        isShowLoader.value = true
        Firebase.messaging.token.addOnFailureListener { exception ->
            isShowLoader.value = false
            exception.printStackTrace()
            showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
        }.addOnSuccessListener { fcmToken ->
            mCompositeDisposable.add(mSignInInteractor.userSignIn(RetrofitUtils.createJsonRequestBody(
                    "email" to mEmail,
                    "password" to mPassword,
                    "fcmToken" to fcmToken,
                    "device" to Constants.DEVICE_TYPE
            ), object : NetworkRequestCallbacks {
                override fun onSuccess(response: Response<*>) {
                    try {
                        isShowLoader.value = false
                        val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                        if (pojoNetworkRequest.isSuccess && response.body() != null) {
                            val pojoUserProfile = response.body() as PojoUserProfile
                            val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoUserProfile.code)
                            when {
                                pojoBackendResponse.isSuccess -> {
                                    //When the request is successful.
                                    mSuccessfulSignIp.value = pojoUserProfile
                                    saveUserDetails(pojoUserProfile)
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //If any other error occurs which results in the backend response not being success.
                                    mSuccessfulSignIp.value = pojoUserProfile
//                                    showMessage.postValue(PojoMessage(message = pojoUserProfile.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //If some error occurs not handled by Backend and results in network request not being success.
//                            mSuccessfulSignIp.value = false
                            showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                        }
                    } catch (e: Exception) {
                        //If some error of any kind occurs while assignment or modification of values.
//                        mSuccessfulSignIp.value = false
                        e.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                    }
                }

                override fun onError(t: Throwable) {
                    //If there is some error parsing the request itself in Interactor
//                    mSuccessfulSignIp.value = false
                    isShowLoader.value = false
                    t.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                }
            }))
        }
    }

    //Saves user details to Shared Pref
    private fun saveUserDetails(pojoUser: PojoUserProfile) {
        mUserPrefsManager.saveUserSession(true, pojoUser.data.accessToken, pojoUser.data.user)
    }

    //LiveData to observe in the Fragment using ViewModel
    fun onSuccessfulSignIp(): LiveData<PojoUserProfile> = mSuccessfulSignIp

}