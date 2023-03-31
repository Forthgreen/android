package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ForgotPasswordInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author shraychona@gmail.com
 * @since 18 May 2020
 *
 * Application parameter is passed in order to subscribe to the android life cycle
 */

class ForgotPasswordViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "ForgotPasswordViewModel"
    }

    //Interactor to hit the required API with the required body
    private val mForgotPasswordInteractor by lazy { ForgotPasswordInteractor() }


    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mSuccessfulApiResponse = MutableLiveData<PojoCommon>()

    //Send details for SignUp as body
    fun forgotPassword(mEmail: String) {
        isShowLoader.value = true
        mCompositeDisposable.add(
            mForgotPasswordInteractor.forgotPassword(RetrofitUtils.createJsonRequestBody(
                "email" to mEmail,
                "requestType" to 2
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
//                                    showMessage.postValue(PojoMessage(message = pojoCommon.message))
                                    mSuccessfulApiResponse.value = pojoCommon
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //If any other error occurs which results in the backend response not being success.
                                    mSuccessfulApiResponse.value = pojoCommon
//                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //If some error occurs not handled by Backend and results in network request not being success.
//                        mSuccessfulApiResponse.value = false
//                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                        }
                    } catch (e: Exception) {
                        //If some error of any kind occurs while assignment or modification of values.
//                    mSuccessfulApiResponse.value = false
                        e.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                    }
                }

                override fun onError(t: Throwable) {
                    //If there is some error parsing the request itself in Interactor
//                mSuccessfulApiResponse.value = false
                    isShowLoader.value = false
                    t.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                }
            })
        )
    }

    //LiveData to observe in the Fragment using ViewModel
    fun onSuccessfulApiResponse(): LiveData<PojoCommon> = mSuccessfulApiResponse

}