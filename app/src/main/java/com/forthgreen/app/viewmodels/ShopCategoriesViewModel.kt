package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.ShopCategoriesInteractor
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoProductCategory
import com.forthgreen.app.repository.models.ShopResponse
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import retrofit2.Response

class ShopCategoriesViewModel(application: Application) : AddBookmarkViewModel(application) {

    companion object {
        private const val TAG = "ShopCategoriesViewModel"
    }

    // Interactor to hit the required API with the required body
    private val mShopCategoriesInteractor by lazy {
        ShopCategoriesInteractor()
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val mCategoriesFetched = MutableLiveData<ShopResponse>()
    private val mCategoriesFetched2 = MutableLiveData<ShopResponse>()

    fun fetchProductsCategory(mShowLoader: Boolean, mPage: Int, mResultSize: Int) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }

        mCompositeDisposable.add(mShopCategoriesInteractor.fetchProductsCategory(
            RetrofitUtils.createJsonRequestBody(
            "page" to mPage),
            object : NetworkRequestCallbacks {
                override fun onSuccess(response: Response<*>) {
                    try {
                        val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                        if (pojoNetworkRequest.isSuccess && response.body() != null) {
                            val pojoProductsCategory = response.body() as ShopResponse
                            val pojoBackendResponse =
                                RetrofitRequest.checkForBackendResponseCode(pojoProductsCategory.code)
                            when {
                                pojoBackendResponse.isSuccess -> {
                                    //When the request is successful.
                                    mCategoriesFetched.value = pojoProductsCategory
                                    mCategoriesFetched2.value = pojoProductsCategory
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //If any other error occurs which results in the backend response not being success.
                                    showMessage.postValue(PojoMessage(message = pojoProductsCategory.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //If some error occurs not handled by Backend and results in network request not being success.
                            showMessage.postValue(
                                PojoMessage(
                                    message = RetrofitRequest.getErrorMessage(
                                        response.errorBody()!!
                                    )
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // If some error of any kind occurs while assignment or modification of values.
                        e.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                    }
                }

                override fun onError(t: Throwable) {
                    // If there is some error parsing the request itself in Interactor
                    t.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                }
            }
        ))
    }

    fun fetchProductsCategory2(mShowLoader: Boolean, mPage: Int, mResultSize: Int) {
        if (mShowLoader) {
            isShowSwipeRefreshLayout.value = true
        }

        mCompositeDisposable.add(mShopCategoriesInteractor.fetchProductsCategory(
            RetrofitUtils.createJsonRequestBody(
                "page" to mPage),
            object : NetworkRequestCallbacks {
                override fun onSuccess(response: Response<*>) {
                    try {
                        val pojoNetworkRequest =
                            RetrofitRequest.checkForNetworkResponseCode(response.code())
                        if (pojoNetworkRequest.isSuccess && response.body() != null) {
                            val pojoProductsCategory = response.body() as ShopResponse
                            val pojoBackendResponse =
                                RetrofitRequest.checkForBackendResponseCode(pojoProductsCategory.code)
                            when {
                                pojoBackendResponse.isSuccess -> {
                                    //When the request is successful.
                                    mCategoriesFetched2.value = pojoProductsCategory
                                }
                                pojoBackendResponse.isSessionExpired -> {
                                    //If the session is expired.
                                    mUserPrefsManager.clearUserPrefs()
                                    isSessionExpired.value = true
                                }
                                else -> {
                                    //If any other error occurs which results in the backend response not being success.
                                    showMessage.postValue(PojoMessage(message = pojoProductsCategory.message))
                                }
                            }
                        } else if (pojoNetworkRequest.isSessionExpired) {
                            // If session expired
                            mUserPrefsManager.clearUserPrefs()
                            isSessionExpired.value = true
                        } else {
                            //If some error occurs not handled by Backend and results in network request not being success.
                            showMessage.postValue(
                                PojoMessage(
                                    message = RetrofitRequest.getErrorMessage(
                                        response.errorBody()!!
                                    )
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // If some error of any kind occurs while assignment or modification of values.
                        e.printStackTrace()
                        showMessage.postValue(PojoMessage(resId = R.string.retrofit_failure))
                    }
                }

                override fun onError(t: Throwable) {
                    // If there is some error parsing the request itself in Interactor
                    t.printStackTrace()
                    showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(t)))
                }
            }
        ))
    }

    // LiveData to observe in the Fragment using ViewModel
    fun onCategoryFetched(): LiveData<ShopResponse> = mCategoriesFetched
    fun onCategoryFetched2(): LiveData<ShopResponse> = mCategoriesFetched2

}