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

class RestaurantDetailsInteractor {

    companion object {
        const val TAG = "RestaurantDetailsInteractor"
    }

    fun fetchRestaurantDetails(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            RestClient.get().fetchRestaurantsDetails(ApplicationGlobal.accessToken, body)
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
            RestClient.get().fetchRestaurantDetailsGuest(body)
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

    fun fetchRestaurantReviews(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().fetchRestaurantsReviews(body)
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

    fun followRestaurant(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().updateFollowStatus(ApplicationGlobal.accessToken, body)
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