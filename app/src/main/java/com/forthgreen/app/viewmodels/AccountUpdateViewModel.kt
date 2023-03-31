package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.AccountUpdateInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoUserUpdate
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File

open class AccountUpdateViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "AccountUpdateViewModel"
    }

    //variables
    private val updateSuccess = MutableLiveData<Boolean>()
    private val updatePass = MutableLiveData<Boolean>()
    //variables
    private val mDeleteAccountSuccess = MutableLiveData<Boolean>()
    //Interactor to hit required API
    private val mAccountUpdateInteractor: AccountUpdateInteractor by lazy {
        AccountUpdateInteractor()
    }

    fun updateDetails(mName: String, mUsername: String, mBio: String, mImageFile: File?) {
        isShowLoader.value = true

        var userImagePart: MultipartBody.Part? = null
        if (mImageFile != null && mImageFile.exists()) {
            userImagePart = RetrofitUtils.createPartFromFile("image", mImageFile)
        }

        mCompositeDisposable.add(mAccountUpdateInteractor.updateUserDetails(RetrofitUtils.createJsonRequestBody(
                "firstName" to mName,
                "username" to mUsername,
                "bio" to mBio

        ), userImagePart, object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoUpdate = response.body() as PojoUserUpdate
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoUpdate.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                saveUserDetails(pojoUpdate)
                                updateSuccess.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                updateSuccess.value = false
                                showMessage.postValue(PojoMessage(message = pojoUpdate.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //response error
                        updateSuccess.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //any other errors
                    isShowLoader.value = false
                    updateSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //retrofit error
                updateSuccess.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun updatePassword(oldPassword: String, newPassword: String) {
        isShowLoader.value = true
        mCompositeDisposable.add(mAccountUpdateInteractor.updateUserDetails(RetrofitUtils.createJsonRequestBody(
                "oldPassword" to oldPassword,
                "newPassword" to newPassword
        ), null, object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoUpdatePass = response.body() as PojoUserUpdate
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoUpdatePass.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                saveUserDetails(pojoUpdatePass)
                                updatePass.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                updatePass.value = false
                                showMessage.postValue(PojoMessage(message = pojoUpdatePass.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //response error
                        updatePass.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //any other errors
                    isShowLoader.value = false
                    updatePass.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //retrofit error
                updatePass.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun deleteAccount() {
        isShowLoader.value = true
        mCompositeDisposable.add(mAccountUpdateInteractor.deleteUserAccount(object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoUpdatePass = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoUpdatePass.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                mDeleteAccountSuccess.value = true
                                logout()
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                mDeleteAccountSuccess.value = false
                                showMessage.postValue(PojoMessage(message = pojoUpdatePass.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //response error
                        mDeleteAccountSuccess.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //any other errors
                    isShowLoader.value = false
                    mDeleteAccountSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //retrofit error
                mDeleteAccountSuccess.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    fun logout() {
        // Do not show on-boardings again when logging out.
        ApplicationGlobal.showOnBoardings = false
        ApplicationGlobal.showNotificationDot = true
        mUserPrefsManager.clearUserPrefs()
        isSessionExpired.value = true
        mUserPrefsManager.updateLoggedInStatus(ValueMapping.getUserAccessGuest())
    }

    //LiveData to observe in the Fragment using ViewModel
    fun onUpdateSuccess(): LiveData<Boolean> = updateSuccess
    fun onUpdatePass(): LiveData<Boolean> = updatePass
    fun onDeleteAccountSuccess(): LiveData<Boolean> = mDeleteAccountSuccess

    //Get data from SharedPrefs
    fun getUserDataFromSharedPrefs(): UserProfile {
        val user = mUserPrefsManager.userProfile
        return if (user != null) {
            user
        } else {
            mUserPrefsManager.clearUserPrefs()
            UserProfile()
        }
    }

    //Saves user details to Shared Pref
    private fun saveUserDetails(pojoUser: PojoUserUpdate) {
        mUserPrefsManager.updateUserProfile(pojoUser.data)
    }
}