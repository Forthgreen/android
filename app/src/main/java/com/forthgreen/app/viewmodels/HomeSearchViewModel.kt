package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.HomeSearchInteractor
import com.forthgreen.app.repository.models.PojoBrandProductSearch
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

/**
 * Created by Chetan Tuteja on 30-May-20.
 * @param application is passed in order to subscribe to the android life cycle.
 */
class HomeSearchViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "HomeSearchViewModel"
    }

    //Interactor to hit the required API with the required body
    private val mHomeSearchInteractor by lazy {
        HomeSearchInteractor()
    }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mBrandProductsFetched = MutableLiveData<PojoBrandProductSearch>()

    //Send Required body as API
    fun searchBrandProducts(mResultSize: Int, mSearchKey: String = "") {
        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
            if (mSearchKey.isNotEmpty()) {
                add("text" to mSearchKey)
            }
            add("limit" to mResultSize)
        }
        mCompositeDisposable.add(mHomeSearchInteractor.searchBrandProduct(RetrofitUtils.createJsonRequestBody(
                *(requestBodyArgs.toTypedArray())
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoBrandProduct = response.body() as PojoBrandProductSearch
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoBrandProduct.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                mBrandProductsFetched.value = pojoBrandProduct
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoBrandProduct.message))
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
                t.printStackTrace()
                showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
            }
        }))
    }

    //LiveData to observe in the Fragment using ViewModel
    fun onSuccessfulBrandProductsFetched(): LiveData<PojoBrandProductSearch> = mBrandProductsFetched
}