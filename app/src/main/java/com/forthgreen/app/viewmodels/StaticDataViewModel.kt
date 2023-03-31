package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.StaticDataInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoStaticData
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import retrofit2.Response

/**
 * Created by Chetan Tuteja on 06-Jun-20.
 * @param application is passed in order to subscribe to the android life cycle
 */
class StaticDataViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "StaticDataViewModel"
    }

    //Interactor to hit the required API with the required body
    private val mStaticDataInteractor by lazy {
        StaticDataInteractor()
    }

    //Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mOnStaticDataReceived = MutableLiveData<PojoStaticData>()

    //Fetch Static Details via API
    fun fetchStaticData() {
        isShowLoader.value = true

        mCompositeDisposable.add(mStaticDataInteractor.fetchStaticData(object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowLoader.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoStaticData = response.body() as PojoStaticData
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoStaticData.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //When the request is successful.
                                mOnStaticDataReceived.value = pojoStaticData
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //If any other error occurs which results in the backend response not being success.
                                showMessage.postValue(PojoMessage(message = pojoStaticData.message))
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

    //LiveData to observe in the Fragment using ViewModel
    fun onStaticDataReceived(): LiveData<PojoStaticData> = mOnStaticDataReceived
}