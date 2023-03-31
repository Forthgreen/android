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
 * @since 01-05-2021
 */
class CommentActionsInteractor {

    companion object {
        private const val TAG = "CommentActionsInteractor"
    }

    fun updateCommentLike(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().updateCommentLike(ApplicationGlobal.accessToken, body)
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

    fun deleteComment(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().deleteComment(ApplicationGlobal.accessToken, body)
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

    fun reportComment(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().reportComment(ApplicationGlobal.accessToken, body)
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