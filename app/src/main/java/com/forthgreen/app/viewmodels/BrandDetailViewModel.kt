package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.BrandDetailInteractor
import com.forthgreen.app.repository.models.PojoBrandDetail
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class BrandDetailViewModel(application: Application) : AddBookmarkViewModel(application) {

    companion object {
        const val TAG = "BrandDetailViewModel"
    }

    //interactor to hit required API
    private val brandDetailInteractor: BrandDetailInteractor by lazy { BrandDetailInteractor() }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mOnFollowBrandSuccess = MutableLiveData<Boolean>()
    private var receiveDetail = MutableLiveData<PojoBrandDetail>()

    fun getBrandDetail(isShowLoading: Boolean, brandRef: String) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(brandDetailInteractor.getBrandDetail(RetrofitUtils.createJsonRequestBody(
                "brandRef" to brandRef
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoBrandDetail = response.body() as PojoBrandDetail
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoBrandDetail.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //request successful
                                receiveDetail.value = pojoBrandDetail
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoBrandDetail.message))
                            }
                        }

                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                isShowSwipeRefreshLayout.value = false
                //show retrofit error
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }

        }))
    }

    //Send Body with Action to Follow a Brand
    fun followBrand(mFollowAction: Boolean, mBrandRef: String) {
        isShowLoader.value = true

        mCompositeDisposable.add(brandDetailInteractor.followBrand(RetrofitUtils.createJsonRequestBody(
                "status" to mFollowAction,
                "brandRef" to mBrandRef
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
                                //When the request is successful.
                                mOnFollowBrandSuccess.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //If any other error occurs which results in the backend response not being success.
                                mOnFollowBrandSuccess.value = false
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else {
                        //If some error occurs not handled by Backend and results in network request not being success.
                        mOnFollowBrandSuccess.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //If some error of any kind occurs while assignment or modification of values.
                    mOnFollowBrandSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //If there is some error parsing the request itself in Interactor
                mOnFollowBrandSuccess.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    //func to observe data in fragment
    fun onReceivingDetail(): LiveData<PojoBrandDetail> = receiveDetail
    fun onFollowBrandSuccess(): LiveData<Boolean> = mOnFollowBrandSuccess
}