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
 * @author Nandita Gandhi
 * @since 21-04-2021
 */
class HomeFeedInteractor {

    companion object {
        private const val TAG = "HomeFeedInteractor"
    }

    fun getFeedData(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            return RestClient.get().fetchFeedData(ApplicationGlobal.accessToken, body)
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
        } else {
            return RestClient.get().fetchFeedDataForGuest(body)
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

    fun getFeedFollowingData(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        if (ApplicationGlobal.isLoggedIn == ValueMapping.getUserAccessLoggedIn()) {
            return RestClient.get().fetchFeedFollowingData(ApplicationGlobal.accessToken, body)
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
        } else {
            return RestClient.get().fetchFeedDataForGuest(body)
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
}