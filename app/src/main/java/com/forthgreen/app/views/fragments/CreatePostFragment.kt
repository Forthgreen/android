package com.forthgreen.app.views.fragments

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Users
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.CreatePostViewModel
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.adapters.CreatePostImageAdapter
import com.forthgreen.app.views.adapters.TagUsersAdapter
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.*
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_comments_list.*
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_create_post.recyclerViewTags
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.show_photo_video_options_bottom_sheet.*
import kotlinx.android.synthetic.main.show_picture_options_bottom_sheet.*
import kotlinx.android.synthetic.main.show_picture_options_bottom_sheet.tvCancel
import kotlinx.android.synthetic.main.show_video_options_bottom_sheet.*
import java.io.*
import java.util.*

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 01-May-21
 */
class CreatePostFragment : BasePictureOptionsFragment(), CreatePostImageAdapter.CreatePostCallback,
        LoadMoreListener, TagUsersAdapter.TagListingCallback {

    companion object {
        const val TAG = "CreatePostFragment"
        private const val MAX_NO_OF_IMAGES = 10
        const val FILE_CONVERSION_TO_MB = 1048576.0
        private const val FILE_SIZE_LIMIT_IN_MB = 150
        private const val FILE_LENGTH_LIMIT_IN_SEC = 60
    }

    // Variables
    private val mAdapter by lazy {
        CreatePostImageAdapter(this)
    }

    private val mCreatePostViewModel by lazy {
        // Getting ViewModel.
        ViewModelProvider(this).get(CreatePostViewModel::class.java)
    }
    private val tagAdapter by lazy {
        TagUsersAdapter(this, this)
    }

    // Variables
    private var mTaggedUsersList = mutableListOf<Users>()
    private var imgPath: String = ""        // Store Image Path of Camera Captured Image.

    private var videoUriString: String? = null
    private var thumbnailUri: String? = null

    private var width = 0
    private var height = 0

    private val postText                    // To get the current text.
        get() = etPostText.trimmedText

    override val layoutId: Int
        get() = R.layout.fragment_create_post

    override val viewModel: BaseViewModel?
        get() = mCreatePostViewModel

    override fun setData() {
        setupViews()
        setupListeners()
        observeProperties()
    }

    private fun setupViews() {
        // Set PushDownAnim
        PushDownAnim.setPushDownAnimTo(btnPost, btnAddPictures, ivPlay, ivClose)

        // Set Toolbar Icon
        toolbar.setNavigationIcon(R.drawable.ic_cancel_dialog)

        // Set Recycler View
        rvPostImages.apply {
            adapter = mAdapter
            layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        enableMediaButton()

        recyclerViewTags.adapter = tagAdapter
        recyclerViewTags.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
//        viewPost.setOnSafeClickListener {
//            // View to increase click area for the post.
//            etPostText.requestFocus()
//            showKeyboard()
//        }

        etPostText.doOnTextChanged { text, start, before, count ->
            if ('@' in text) {
                val pos = text.lastIndexOf('@')
                if (pos == 0 || text.get(pos - 1) == ' ') {
                    val str = text.substring(pos + 1)
                    mCreatePostViewModel.getUsersToTag(false, str)
                }
            } else {
                recyclerViewTags.gone()
                rvPostImages.visible()
                btnAddPictures.visible()
            }
        }
        ivClose.setOnSafeClickListener {
            groupVideo.gone()
            enableMediaButton()
            thumbnailUri = null
            videoUriString = null
            checkForButtonStatus()
        }
        btnAddPictures.setOnSafeClickListener {
            if (isAllGranted(Permission.READ_EXTERNAL_STORAGE)) {
                showImageBottomSheet()
            } else {
                askForPermissions(Permission.READ_EXTERNAL_STORAGE) { result ->
                    if (result.isAllGranted(Permission.READ_EXTERNAL_STORAGE)) {
                        showImageBottomSheet()
                    } else {
                        showMessage(R.string.enable_storage_permission)
                    }
                }
            }
//            // Check for permissions and show bottom sheet accordingly, else ask for it.
//            if (isAllGranted(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)) {
//                showImageBottomSheet()
//            } else {
//                askForPermissions(
//                    Permission.READ_EXTERNAL_STORAGE,
//                    Permission.WRITE_EXTERNAL_STORAGE
//                ) { result ->
//                    if (result.isAllGranted(
//                            Permission.READ_EXTERNAL_STORAGE,
//                            Permission.WRITE_EXTERNAL_STORAGE
//                        )
//                    ) {
//                        showImageBottomSheet()
//                    } else {
//                        showMessage(R.string.storage_permission_needed)
//                    }
//                }
//            }
        }
        ivPlay.setOnClickListener {
            performTransaction(PlayVideoFragment.newInstance(videoUriString ?: ""),
                    PlayVideoFragment.TAG)
        }
        btnPost.setOnSafeClickListener {
            val postText = etPostText.trimmedText
            val postImages = mAdapter.fetchSelectedImages()

            if (postText.isEmpty() && postImages.isEmpty() && videoUriString.isNullOrEmpty()) {
                showMessage(R.string.create_post_empty_error)
                return@setOnSafeClickListener
            }

            val usersToRemove = mutableListOf<Users>()
            mTaggedUsersList.forEach { user ->
                if (!(postText.contains(user.name))) {
                    usersToRemove.add(user)
                }
            }
            mTaggedUsersList.removeAll(usersToRemove)
            var str = etPostText.text.toString()
            str = " $str"
            str = str.replace(" @", " ")

            videoUriString?.let { uri ->
                val time = getVideoDuration(requireContext(), Uri.parse(uri))
                if (time != null) {
                    if ((time / 1000) > FILE_LENGTH_LIMIT_IN_SEC) {
                        showMessage(message = getString(
                            R.string.create_post_file_length_error,
                            FILE_LENGTH_LIMIT_IN_SEC
                        ))
                        return@setOnSafeClickListener
                    }
                }
            }


            val videoMedia: File? = if (videoUriString != null) {
                File(videoUriString!!)
            } else {
                null
            }
            val thumbnail: File? = if (thumbnailUri != null) {
                File(thumbnailUri)
            } else {
                null
            }

            val type = if (postText.isNotEmpty() && postImages.isNotEmpty()) {
                ValueMapping.onPostWithImageNText()
            } else if (postText.isNotEmpty() && !videoUriString.isNullOrEmpty()) {
                ValueMapping.onPostWithVideoNText()
            } else if (postImages.isNotEmpty()) {
                ValueMapping.onPostWithImageOnly()
            } else if (!videoUriString.isNullOrEmpty()) {
                ValueMapping.onPostWithVideoOnly()
            } else {
                ValueMapping.onPostWithTextOnly()
            }
            videoUriString?.let { uri ->
                width = getVideoWidthOrHeight(File(uri), "width")
                height = getVideoWidthOrHeight(File(uri), "height")
            }

            mCreatePostViewModel.createPost(
                showLoader = true,
                str.trim(),
                *postImages.toTypedArray(),
                tagUserList = mTaggedUsersList,
                video = videoMedia,
                thumbnail = thumbnail,
                postType = type,
                width = width,
                    height = height,
            )
        }
    }

    // Helper method to observe data via ViewModel.
    private fun observeProperties() {
        mCreatePostViewModel.createPostSetupDone.observe(viewLifecycleOwner, { setupDone ->
            if (setupDone) {
                hideKeyboard()
                supportFragmentManager().popBackStack()
            }
        })
        etPostText.doOnTextChanged { _, _, _, _ ->
            checkForButtonStatus()
        }

        mCreatePostViewModel.onTagsListSuccess().observe(viewLifecycleOwner, { tagList ->
            if (tagList.data.isEmpty()) {
                recyclerViewTags.gone()
                btnAddPictures.visible()
                tagAdapter.submitList(emptyList(), false)
            } else {
                recyclerViewTags.visible()
                rvPostImages.gone()
                btnAddPictures.gone()
                tagAdapter.submitList(tagList.data, false)
            }
        })
    }

    // Check for the number of images and show bottom sheet accordingly.
    private fun showImageBottomSheet() {
        if (mAdapter.itemCount < MAX_NO_OF_IMAGES) {
            photoVideoOptionsBottomSheet.show()
        } else {
            showMessage(R.string.image_selection_error)
        }
    }

    // Helper method to check and set button status.
    private fun checkForButtonStatus() {
        // Enable-Disable Button if there is not text or pics added.
        if (mAdapter.itemCount == 0 && postText.isEmpty() && videoUriString.isNullOrEmpty()) {
            btnPost.apply {
                isEnabled = false
                background = ContextCompat.getDrawable(requireContext(), R.drawable.drawable_button_create_post_disabled)
            }
        } else {
            btnPost.apply {
                isEnabled = true
                background = ContextCompat.getDrawable(requireContext(), R.drawable.drawable_button_create_post)
            }
        }
    }

    private val photoVideoOptionsBottomSheet
        get() = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).apply {
            customView(viewRes = R.layout.show_photo_video_options_bottom_sheet,
                noVerticalPadding = true)

            tvPhoto.setOnClickListener {
                this.dismiss()
                showPictureOptionsBottomSheet(isShowSelectionOptions = true, isVideo = false)
            }
            tvVideo.setOnClickListener {
                this.dismiss()
                showVideoOptionsBottomSheet(false)
            }
            tvCancel.setOnClickListener {
                this.dismiss()
            }
        }

    private fun getVideoDuration(context: Context, source: Uri): Long? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, source)
        val duration = retriever.extractMetadata(METADATA_KEY_DURATION)
//        width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
//        height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        retriever.release()
        return duration?.toLongOrNull()
    }

    private fun getVideoWidthOrHeight(file: File, widthOrHeight: String): Int {
        var retriever: MediaMetadataRetriever? = null
        var bmp: Bitmap? = null
        var inputStream: FileInputStream? = null
        var mWidthHeight = 0
        try {
            retriever = MediaMetadataRetriever()
            inputStream = FileInputStream(file.getAbsolutePath())
            retriever.setDataSource(inputStream.getFD())
            bmp = retriever.getFrameAtTime()
            if (widthOrHeight.equals("width")) {
                mWidthHeight = bmp!!.getWidth()
            } else {
                mWidthHeight = bmp!!.getHeight()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            if (retriever != null) {
                retriever.release()
            }
            if (inputStream != null) {
                inputStream.close()
            }
        }
        return mWidthHeight
    }

    private fun addImagesToAdapter(galleryIntentData: Intent?) {
        // If multiple images are selected
        galleryIntentData?.clipData?.let { clipData ->
            val imageList = arrayListOf<Uri>()
            for (i in 0 until clipData.itemCount) {
                imageList.add(clipData.getItemAt(i).uri)
            }
            if (imageList.size == 1) {
                return
            }
            // Submit only up to 10 images, if more are selected, trim the list to 10.
            if (mAdapter.itemCount < MAX_NO_OF_IMAGES) {
                // If more than 10 images are selected at once, trim up to 10.
                val currentImageSize = mAdapter.itemCount
                val pickedImageSize = imageList.size
                val slotsLeft = MAX_NO_OF_IMAGES - currentImageSize

                if (pickedImageSize > slotsLeft) {
                    // Submit the images according to the left space.
                    mAdapter.submitList(imageList.subList(0, slotsLeft))
                    checkForButtonStatus()
                    viewPost.gone()
                    showMessage(R.string.image_selection_error)
                } else {
                    mAdapter.submitList(imageList)
                    checkForButtonStatus()
                    viewPost.gone()
                }

            } else {
                showMessage(R.string.image_selection_error)
            }
            return
        }
        // Single Image Selected
//        galleryIntentData?.data?.let { uri ->
//            // Submit if the item count is less than 10
//            if (mAdapter.itemCount < MAX_NO_OF_IMAGES) {
//                mAdapter.submitImage(uri)
//                checkForButtonStatus()
//                viewPost.gone()
//            } else {
//                showMessage(R.string.image_selection_error)
//            }
//        }
    }

    override fun imageRemoved() {
        // Check if any image is selected, then hide the view else show it if none are selected.
        if (mAdapter.itemCount == 0) {
            viewPost.visible()
            enableMediaButton()
        } else {
            viewPost.gone()
            disableMediaButton()
        }
        checkForButtonStatus()
    }

    override fun onGettingImageFile(file: File) {
        // Submit if the item count is less than 10
        if (mAdapter.itemCount < MAX_NO_OF_IMAGES) {
            mAdapter.submitImage(Uri.fromFile(file))
            checkForButtonStatus()
            viewPost.gone()
        } else {
            showMessage(R.string.image_selection_error)
        }
        disableMediaButton()
    }

    override fun onGettingVideoUri(uri: Uri, type: Int) {
        val videoUri = if (type == ValueMapping.onCaptureVideoType()) {
            uri
        } else {
            Uri.fromFile(fileFromContentUri(requireContext(), uri))
        }
        videoUriString = getPath(requireContext(), videoUri)
        videoUriString?.let { uri ->
            // Check for File Size and show error if needed.
            if ((File(uri).length() / FILE_CONVERSION_TO_MB) > FILE_SIZE_LIMIT_IN_MB) {
                showMessage(message = getString(
                    R.string.create_post_file_size_error,
                    FILE_SIZE_LIMIT_IN_MB
                ))
                return
            }
        }
        val bitmap = ThumbnailUtils.createVideoThumbnail(
            videoUriString!!, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
        )
        thumbnailUri = bitmapToFile(bitmap!!).toString()

        // fix this and also load thumbnail using glide into a imageView because video will be
        // played on other screen
        disableMediaButton()
        checkForButtonStatus()
        rvPostImages.gone()
        groupVideo.visible()
        Glide.with(this)
                .load(thumbnailUri)
                .into(ivVideo)
    }

    private fun fileFromContentUri(context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri)
        val fileName =
                "temp_file" + "VID_${System.currentTimeMillis()}" + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(requireContext())

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }

    private fun getPath(context: Context, uri: Uri): String? {
        val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver
                    .query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            addImagesToAdapter(data)
            disableMediaButton()
        }
    }

    override fun onTagListingClick(taggedUser: Users) {
        var fullComment: String = etPostText.text.toString()
        if ('@' in fullComment) {
            val pos = fullComment.lastIndexOf('@')
            fullComment = fullComment.substring(0, pos + 1)
            fullComment += taggedUser.name + " "
        }
        etPostText.setText(fullComment)
        etPostText.setSelection(etPostText.text.length)
        mTaggedUsersList.add(
                Users(
                        _id = taggedUser._id,
                        name = taggedUser.name,
                        type = taggedUser.type
                )
        )
        recyclerViewTags.gone()
        rvPostImages.visible()
        btnAddPictures.visible()
    }

    override fun onLoadMore() {
    }

    private fun disableMediaButton() {
        btnAddPictures.apply {
            isEnabled = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorEditTextBottomGrey))
        }
    }

    private fun enableMediaButton() {
        // Enable button
        btnAddPictures.apply {
            isEnabled = true
            setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
        }
    }

    private fun performTransaction(frag: Fragment, fragmentTag: String) {
        val perform = activity as BaseAppCompactActivity
        perform.doFragmentTransaction(supportFragmentManager(), R.id.flFragContainer,
                frag, fragmentTag, isAddFragment = true)
    }

    override fun onDestroyView() {
        mTaggedUsersList.clear()
        super.onDestroyView()
    }
}