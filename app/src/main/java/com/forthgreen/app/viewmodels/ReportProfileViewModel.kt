package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ReportProfileInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 03-05-2021
 */
class ReportProfileViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "ReportProfileViewModel"
    }

    // Interactor to hit required API
    private val mReportProfileInteractor by lazy { ReportProfileInteractor() }
    private val mOnReportSuccess = MutableLiveData<Boolean>()

    fun reportProfile(mShowLoader: Boolean, mUserReportType: Int, mUserRef: String, mFeedback: String) {
        if (mShowLoader) {
            isShowLoader.value = true
        }

        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
            add("userRef" to mUserRef)
            add("userReportType" to mUserReportType)
            if (mFeedback.isNotEmpty()) {
                add("feedback" to mFeedback)
            }
        }
        mCompositeDisposable.add(mReportProfileInteractor.reportProfile(RetrofitUtils.createJsonRequestBody(
                *(requestBodyArgs.toTypedArray())
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
                                mOnReportSuccess.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                mOnReportSuccess.value = false
                                // If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If the session is expired.
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        mOnReportSuccess.value = false
                        // If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    isShowLoader.value = false
                    mOnReportSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                mOnReportSuccess.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    // Live data to be observed in fragment
    fun onSuccessfulProfileReported(): LiveData<Boolean> = mOnReportSuccess
}