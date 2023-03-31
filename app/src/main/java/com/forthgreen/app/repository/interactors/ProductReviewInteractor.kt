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

class ProductReviewInteractor {

    companion object {
        const val TAG = "ProductReviewInteractor"
    }

    //Hit API via retrofit
    fun addReview(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().addReview(ApplicationGlobal.accessToken, body)
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