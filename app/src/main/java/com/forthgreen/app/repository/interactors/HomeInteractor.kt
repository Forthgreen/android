package com.forthgreen.app.repository.interactors

import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RestClient
import com.forthgreen.app.utils.ApplicationGlobal
import com.forthgreen.app.utils.ValueMapping
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import retrofit2.Response

class HomeInteractor {

    companion object {
        const val TAG = "HomeInteractor"
    }

    fun getBrandList(
        body: RequestBody,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            RestClient.get().getBrandList(ApplicationGlobal.accessToken, body)
        } else {
            RestClient.get().getBrandListGuest(body)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onComplete() {
                }

                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(e: Throwable) {
                    networkRequestCallbacks.onError(e)
                }
            })
    }

    fun getProductList(
        body: RequestBody,
        userLoggedIn: Int,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return if (userLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            RestClient.get().getProductList(ApplicationGlobal.accessToken, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<*>>() {
                    override fun onComplete() {
                    }

                    override fun onNext(response: Response<*>) {
                        networkRequestCallbacks.onSuccess(response)
                    }

                    override fun onError(e: Throwable) {
                        networkRequestCallbacks.onError(e)
                    }
                })
        } else {
            RestClient.get().getProductListGuest(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<*>>() {
                    override fun onComplete() {
                    }

                    override fun onNext(response: Response<*>) {
                        networkRequestCallbacks.onSuccess(response)
                    }

                    override fun onError(e: Throwable) {
                        networkRequestCallbacks.onError(e)
                    }
                })
        }
    }
}