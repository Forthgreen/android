package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.MyBrandsInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoMyBrands
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class MyBrandsViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "MyBrandsViewModel"
    }

    //interactor to hit required API
    private val myBrandsInteractor: MyBrandsInteractor by lazy { MyBrandsInteractor() }
    private var receivedData = MutableLiveData<PojoMyBrands>()

    fun getBookmarkedBrands(isShowLoading: Boolean, mPage: Int, resultSize: Int, refType: Int) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(
            myBrandsInteractor.getBookmarkedBrands(RetrofitUtils.createJsonRequestBody(
                "refType" to refType,
                "page " to mPage,
                "limit" to Int.MAX_VALUE
            ), object : NetworkRequestCallbacks {
                override fun onSuccess(response: Response<*>) {
                    try {
                        isShowSwipeRefreshLayout.value = false
                        val pojoNetworkRequest =
                            RetrofitRequest.checkForNetworkResponseCode(response.code())
                        if (pojoNetworkRequest.isSuccess && response.body() != null) {
                            val pojoMyBrands = response.body() as PojoMyBrands
                            val pojoBackendResponse =
                                RetrofitRequest.checkForBackendResponseCode(pojoMyBrands.code)
                            when {
                                //request successful
                                pojoBackendResponse.isSuccess -> {
                                    receivedData.postValue(pojoMyBrands)
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //backend error
                                    showMessage.postValue(PojoMessage(message = pojoMyBrands.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //show networkRequest error
                            showMessage.postValue(
                                PojoMessage(
                                    message = RetrofitRequest.getErrorMessage(
                                        response.errorBody()!!
                                    )
                                )
                            )
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

            })
        )
    }

    //func to observe data in fragment
    fun onReceivingMyBrands(): LiveData<PojoMyBrands> = receivedData
}