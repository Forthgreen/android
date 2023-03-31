package com.forthgreen.app.repository.interactors

import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

/**
 * @author ShrayChona@gmail.com
 * @since 20/3/19
 */
class SignUpInteractor {

    fun userSignUp(body: RequestBody, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().userSignUp(body)
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