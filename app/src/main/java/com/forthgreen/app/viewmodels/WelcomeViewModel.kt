package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.WelcomeInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoUserProfile
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File

/**
 * @author shraychona@gmail.com
 * @since 20 May 2020
 *
 * Application parameter is passed in order to subscribe to the android life cycle
 */

class WelcomeViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "WelcomeViewModel"
    }

    //Interactor to hit the required API with the required body
    private val mWelcomeInteractor by lazy { WelcomeInteractor() }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mSuccessfulSignIn = MutableLiveData<Boolean>()

    //Send details for SignUp as body
    fun userSocialLogin(fullName: String, mEmail: String, gender: Int, dateOfBirth: String, socialId: String, socialToken: String, socialIdentifier: Int, mUserImage: File?) {
        isShowLoader.value = true
        Firebase.messaging.token.addOnFailureListener { exception ->
            isShowLoader.value = false
            exception.printStackTrace()
            showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
        }.addOnSuccessListener { fcmToken ->
            var userImagePart: MultipartBody.Part? = null
            if (mUserImage != null && mUserImage.exists()) {
                userImagePart = RetrofitUtils.createPartFromFile("image", mUserImage)
            }

            mCompositeDisposable.add(mWelcomeInteractor.userSocialLogin(RetrofitUtils.createJsonRequestBody(
                    "email" to mEmail,
                    "firstName" to fullName,
                    "dateOfBirth" to dateOfBirth,
                    "fullName" to fullName,
                    "gender" to gender,
                    "fcmToken" to fcmToken,
                    "socialId" to socialId,
                    "socialToken" to socialToken,
                    "socialIdentifier" to socialIdentifier,
                    "device" to Constants.DEVICE_TYPE
            ), userImagePart, object : NetworkRequestCallbacks {
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
                                    mSuccessfulSignIn.value = true
                                    saveUserDetails(pojoUserProfile)
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //If any other error occurs which results in the backend response not being success.
                                    mSuccessfulSignIn.value = false
                                    showMessage.postValue(PojoMessage(message = pojoUserProfile.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //If some error occurs not handled by Backend and results in network request not being success.
                            mSuccessfulSignIn.value = false
                            showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                        }
                    } catch (e: Exception) {
                        //If some error of any kind occurs while assignment or modification of values.
                        mSuccessfulSignIn.value = false
                        e.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                    }
                }

                override fun onError(t: Throwable) {
                    //If there is some error parsing the request itself in Interactor
                    mSuccessfulSignIn.value = false
                    isShowLoader.value = false
                    t.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                }
            }))
        }
    }

    fun updateLoggedInStatus(status: Int) {
        mUserPrefsManager.updateLoggedInStatus(status)
    }

    //Saves user details to Shared Pref
    private fun saveUserDetails(pojoUser: PojoUserProfile) {
        mUserPrefsManager.saveUserSession(true, pojoUser.data.accessToken, pojoUser.data.user)
    }

    //LiveData to observe in the Fragment using ViewModel
    fun onSuccessfulSignIn(): LiveData<Boolean> = mSuccessfulSignIn

}