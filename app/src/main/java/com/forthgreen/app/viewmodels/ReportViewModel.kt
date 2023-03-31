package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ReportInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class ReportViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "ReportViewModel"
    }

    //boolean for confirmation
    private var reported = MutableLiveData<Boolean>()
    private var reviewReported = MutableLiveData<Boolean>()
    private val mReportInteractor: ReportInteractor by lazy { ReportInteractor() }

    fun reportBrand(reportType: Int, brandRef: String, brandReportType: Int, feedback: String) {
        isShowLoader.value = true
        mCompositeDisposable.add(mReportInteractor.report(RetrofitUtils.createJsonRequestBody(
                "reportType" to reportType,
                "brandRef" to brandRef,
                "brandReportType" to brandReportType,
                "feedback" to feedback
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoReport = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoReport.code)
                        when {
                            //request successful
                            pojoBackendResponse.isSuccess -> {
                                reported.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                reported.value = false
                                showMessage.postValue(PojoMessage(message = pojoReport.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        reported.value = false
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }

                } catch (e: Exception) {
                    isShowLoader.value = false
                    reported.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                reported.value = false
                isShowLoader.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    fun reportReview(reportType: Int, reviewRef: String) {
        isShowLoader.value = true
        mCompositeDisposable.add(mReportInteractor.report(RetrofitUtils.createJsonRequestBody(
                "reportType" to reportType,
                "reviewRef" to reviewRef
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoReport = response.body() as PojoCommon
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoReport.code)
                        when {
                            //request successful
                            pojoBackendResponse.isSuccess -> {
                                reviewReported.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                reviewReported.value = false
                                showMessage.postValue(PojoMessage(message = pojoReport.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        reviewReported.value = false
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }

                } catch (e: Exception) {
                    isShowLoader.value = false
                    reviewReported.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                reviewReported.value = false
                isShowLoader.value = false
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }


    //live data to observe back in fragment
    fun onSuccessfulReport(): LiveData<Boolean> = reported

    fun onSuccessfulReviewReport(): LiveData<Boolean> = reviewReported
}