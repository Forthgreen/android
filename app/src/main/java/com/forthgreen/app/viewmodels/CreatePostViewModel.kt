package com.forthgreen.app.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.workDataOf
import com.forthgreen.app.repository.models.Users
import com.forthgreen.app.workers.CreatePostWorker
import com.google.gson.Gson
import java.io.File

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 04-May-21
 *
 * @param application is passed in order to subscribe to the android life cycle.
 */
class CreatePostViewModel(application: Application) : TagViewModel(application) {

    companion object {
        private const val TAG = "CreatePostViewModel"
        private const val CREATE_POST_UNIQUE_KEY = "CREATE_POST_UNIQUE_KEY"

        const val KEY_TEXT = "text"
        const val KEY_MEDIA = "postMedia"
        const val KEY_TAGS = "tags"
        const val KEY_VIDEO = "video"
        const val KEY_THUMBNAIL = "thumbnailImage"
        const val KEY_TYPE = "postType"
        const val KEY_WIDTH = "videoWidth"
        const val KEY_HEIGHT = "videoHeight"
    }

    // Variables
    private val appContext by lazy {
        application.applicationContext
    }

    // Required PojoModel as MutableLiveData or Boolean in case of just confirmation.
    private val _createPostSetupDone = MutableLiveData<Boolean>()

    // LiveData to observe in the Fragment using ViewModel
    val createPostSetupDone: LiveData<Boolean>
        get() = _createPostSetupDone

    fun createPost(
        showLoader: Boolean,
        postText: String,
        vararg postImages: Uri,
        tagUserList: List<Users>,
        video: File? = null,
        thumbnail: File? = null,
        postType: Int,
        width: Int,
        height: Int,
    ) {
        if (showLoader) {
            isShowLoader.value = true
        }

        val workManagerData = workDataOf(
            KEY_TEXT to postText,
            KEY_MEDIA to postImages.map { imageUri -> imageUri.toString() }.toTypedArray(),
            KEY_TAGS to tagUserList.map { tag -> Gson().toJson(tag) }.toTypedArray(),
            KEY_VIDEO to video?.absolutePath,
            KEY_THUMBNAIL to thumbnail?.absolutePath,
            KEY_TYPE to postType,
            KEY_WIDTH to width,
            KEY_HEIGHT to height,
        )

        // Create a Work Request and Append the work in chain if any work with same key is already
        // scheduled.
        CreatePostWorker.schedule(appContext, workManagerData, CREATE_POST_UNIQUE_KEY)

        // Stop Loader and update Status after creation.
        isShowLoader.value = false
        _createPostSetupDone.postValue(true)
    }
}