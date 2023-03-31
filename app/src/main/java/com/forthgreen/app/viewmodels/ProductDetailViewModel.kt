package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ProductDetailInteractor
import com.forthgreen.app.repository.models.PojoCommon
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoProductDetail
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class ProductDetailViewModel(application: Application) : AddBookmarkViewModel(application) {
    companion object {
        const val TAG = "ProductDetailViewModel"
    }

    private var receiveDetail = MutableLiveData<PojoProductDetail>()
    private val saveActionSuccess = MutableLiveData<Boolean>()

    //interactor to hit required API
    private val productDetailInteractor: ProductDetailInteractor by lazy { ProductDetailInteractor() }

    fun getProductDetail(isShowLoading: Boolean, page: Int, resultSize: Int, productRef: String) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(productDetailInteractor.getProductDetail(RetrofitUtils.createJsonRequestBody(
                "page" to page,
                "limit" to resultSize,
                "productRef" to productRef
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoProductDetail = response.body() as PojoProductDetail
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoProductDetail.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //request successful
                                receiveDetail.value = pojoProductDetail
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoProductDetail.message))
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

    fun reportProductVisit(productRef: String) {
        mCompositeDisposable.add(productDetailInteractor.reportProductVisit(RetrofitUtils.createJsonRequestBody(
                "productRef" to productRef
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
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //If some error of any kind occurs while assignment or modification of values.
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    fun reportWebsiteClick(productRef: String) {
        mCompositeDisposable.add(productDetailInteractor.reportWebsiteClick(RetrofitUtils.createJsonRequestBody(
                "productRef" to productRef
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
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        //If some error occurs not handled by Backend and results in network request not being success.
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    //If some error of any kind occurs while assignment or modification of values.
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                //If there is some error parsing the request itself in Interactor
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    fun saveProduct(saveRestaurant: Boolean, productRef: String, showLoader: Boolean = true) {
        if (showLoader) {
            isShowLoader.value = true
        }

        mCompositeDisposable.add(productDetailInteractor.saveProduct(RetrofitUtils.createJsonRequestBody(
                "status" to saveRestaurant,
                "productRef" to productRef
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
                                saveActionSuccess.value = true
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                // If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                // If any other error occurs which results in the backend response not being success.
                                saveActionSuccess.value = false
                                showMessage.postValue(PojoMessage(message = pojoCommon.message))
                            }
                        }
                    } else if (pojoNetworkRequest.isSessionExpired) {
                        // If session expired
                        mUserPrefsManager.clearUserPrefs()
                        isSessionExpired.value = true
                    } else {
                        // If some error occurs not handled by Backend and results in network request not being success.
                        saveActionSuccess.value = false
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    // If some error of any kind occurs while assignment or modification of values.
                    saveActionSuccess.value = false
                    e.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                }
            }

            override fun onError(t: Throwable) {
                // If there is some error parsing the request itself in Interactor
                saveActionSuccess.value = false
                isShowLoader.value = false
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    //func to observe data in fragment
    fun onReceivingDetail(): LiveData<PojoProductDetail> = receiveDetail
    fun onSaveActionSuccess(): LiveData<Boolean> = saveActionSuccess

    //Return User Id of the current user.
    fun fetchUserId(): String {
        val user = mUserPrefsManager.userProfile
        return user?._id ?: ""
    }
}