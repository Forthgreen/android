package com.forthgreen.app.repository.interactors

import com.forthgreen.app.repository.networkrequest.NetworkRequestCallbacks
import com.forthgreen.app.repository.networkrequest.RestClient
import com.forthgreen.app.utils.ApplicationGlobal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AccountUpdateInteractor {

    companion object {
        const val TAG = "AccountUpdateInteractor"
    }

    //Hit API via retrofit
    fun updateUserDetails(body: RequestBody, image: MultipartBody.Part?, networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().updateDetails(ApplicationGlobal.accessToken, body, image)
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

    //Hit API via retrofit
    fun deleteUserAccount(networkRequestCallbacks: NetworkRequestCallbacks): Disposable {
        return RestClient.get().deleteAccount(ApplicationGlobal.accessToken)
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