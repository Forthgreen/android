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

/**
 * Created by Chetan Tuteja on 23-Oct-20.
 */
class RestaurantListingInteractor {

    companion object {
        private const val TAG = "RestaurantListingIntera"
    }

    //Hits API via Retrofit
    fun fetchRestaurants(
        body: RequestBody,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            RestClient.get().fetchRestaurants(ApplicationGlobal.accessToken, body)
        } else {
            RestClient.get().fetchRestaurantsGuest(body)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }

                override fun onComplete() {
                }
            })
    }

    fun fetchRestaurantsForMap(
        body: RequestBody,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().fetchRestaurantsForMap(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }

                override fun onComplete() {
                }
            })
    }
}