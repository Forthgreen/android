package com.forthgreen.app.repository.interactors

import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RestClient
import com.forthgreen.app.utils.ApplicationGlobal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import retrofit2.Response

/**
 * @author Nandita Gandhi
 * @since 05-05-2021
 */
class NotificationDetailsInteractor {

    companion object {
        private const val TAG = "NotificationDetailsInteractor"
    }

    fun fetchNotificationDetails(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().fetchNotificationDetails(ApplicationGlobal.accessToken, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<*>>() {
                    override fun onNext(response: Response<*>) {
                        networkRequestCallbacks.onSuccess(response)
                    }

                    override fun onError(e: Throwable) {
                        networkRequestCallbacks.onError(e)
                    }

                    override fun onComplete() {
                    }
                })
    }

    fun fetchPostDetails(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().fetchPostDetails(ApplicationGlobal.accessToken, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(e: Throwable) {
                    networkRequestCallbacks.onError(e)
                }

                override fun onComplete() {
                }
            })
    }

    fun fetchCommentsList(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().fetchComments(ApplicationGlobal.accessToken, body)
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