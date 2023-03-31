package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.R
import com.forthgreen.app.repository.interactors.HomeInteractor
import com.forthgreen.app.repository.models.PojoBrandList
import com.forthgreen.app.repository.models.PojoMessage
import com.forthgreen.app.repository.models.PojoProductList
import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RetrofitRequest
import com.forthgreen.app.repository.networkrequest.RetrofitUtils
import com.forthgreen.app.utils.ApplicationGlobal.Companion.isLoggedIn
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.fragments.CategoryProductsFragment
import org.json.JSONArray
import retrofit2.Response

class HomeViewModel(application: Application) : AddBookmarkViewModel(application) {

    companion object {
        const val TAG = "HomeViewModel"
    }

    //Interactor to hit required API with body
    private val mHomeInteractor by lazy { HomeInteractor() }
    private var receivedProductList = MutableLiveData<PojoProductList>()
    private var receivedBrandList = MutableLiveData<PojoBrandList>()

    //func to get productList
    fun getProductList(
            isShowLoading: Boolean, categoryIds: List<Int>,
            mPage: Int, resultSize: Int, filter: List<Int> = listOf(), sort: Int = CategoryProductsFragment.DEFAULT_SORT_VALUE,
            mGender: Int = ValueMapping.onBothGenderFilters(),
    ) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        val requestBodyArgs = arrayListOf<Pair<String, Any>>().apply {
            add("page" to mPage)
            add("limit" to resultSize)
            add("category" to JSONArray(categoryIds))
            if (filter.isNotEmpty()) {
                add("filter" to JSONArray(filter))
            }
            if (sort != CategoryProductsFragment.DEFAULT_SORT_VALUE) {
                add("sort" to sort)
            }
            add("gender" to mGender)
        }
        mCompositeDisposable.add(mHomeInteractor.getProductList(
            RetrofitUtils.createJsonRequestBody(
                *(requestBodyArgs.toTypedArray())
            ), isLoggedIn, object : NetworkRequestCallbacks {
                override fun onSuccess(response: Response<*>) {
                    try {
                        isShowSwipeRefreshLayout.value = false
                        val pojoNetworkRequest =
                            RetrofitRequest.checkForNetworkResponseCode(response.code())
                        if (pojoNetworkRequest.isSuccess && response.body() != null) {
                            val pojoProducts = response.body() as PojoProductList
                            val pojoBackendResponse =
                                RetrofitRequest.checkForBackendResponseCode(pojoProducts.code)
                            when {
                                pojoBackendResponse.isSuccess -> {
                                    //request successful
                                receivedProductList.value = pojoProducts
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoProducts.message))
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
                    isShowSwipeRefreshLayout.value = false
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

    //func to get brandList
    fun getBrandList(isShowLoading: Boolean, categoryIds: List<Int>, mPage: Int, resultSize: Int) {
        if (isShowLoading) {
            isShowSwipeRefreshLayout.value = true
        }
        mCompositeDisposable.add(mHomeInteractor.getBrandList(RetrofitUtils.createJsonRequestBody(
                "category" to categoryIds.toTypedArray(),
                "page" to mPage,
                "limit" to resultSize
        ), object : NetworkRequestCallbacks {
            override fun onSuccess(response: Response<*>) {
                try {
                    isShowSwipeRefreshLayout.value = false
                    val pojoNetworkRequest = RetrofitRequest.checkForNetworkResponseCode(response.code())
                    if (pojoNetworkRequest.isSuccess && response.body() != null) {
                        val pojoBrands = response.body() as PojoBrandList
                        val pojoBackendResponse = RetrofitRequest.checkForBackendResponseCode(pojoBrands.code)
                        when {
                            pojoBackendResponse.isSuccess -> {
                                //request successful
                                receivedBrandList.value = pojoBrands
                            }
                            pojoBackendResponse.isSessionExpired -> {
                                //If the session is expired.
                                mUserPrefsManager.clearUserPrefs()
                                isSessionExpired.value = true
                            }
                            else -> {
                                //backend error
                                showMessage.postValue(PojoMessage(message = pojoBrands.message))
                            }
                        }

                    } else {
                        //show networkRequest error
                        showMessage.postValue(PojoMessage(message = RetrofitRequest.getErrorMessage(response.errorBody()!!)))
                    }
                } catch (e: Exception) {
                    isShowSwipeRefreshLayout.value = false
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

    //func to observe data in fragment
    fun onReceivingBrands(): LiveData<PojoBrandList> = receivedBrandList

    fun onReceivingProducts(): LiveData<PojoProductList> = receivedProductList
}

