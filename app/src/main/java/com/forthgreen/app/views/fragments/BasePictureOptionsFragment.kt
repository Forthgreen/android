package com.forthgreen.app.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.forthgreen.app.R
import com.forthgreen.app.utils.GetSampledImage
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.ImageUtils
import com.forthgreen.app.views.utils.copyInputStreamToFile
import kotlinx.android.synthetic.main.show_picture_options_bottom_sheet.view.*
import kotlinx.android.synthetic.main.show_picture_options_bottom_sheet.view.tvCancel
import kotlinx.android.synthetic.main.show_video_options_bottom_sheet.view.*
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by ShrayChona on 03-10-2018.
 * @description extend this class for basic fragment with support of uploading images
 */
abstract class BasePictureOptionsFragment : BaseFragment(), GetSampledImage.SampledImageAsyncResp {

    companion object {
        const val GALLERY_IMAGE_REQUEST = 444
        private const val REQUEST_IMAGE_CAPTURE = 543
        private const val GALLERY_VIDEO_REQUEST = 445
        private const val REQUEST_VIDEO_CAPTURE = 544
    }

    // Variables
    private var currentPhotoPath: String = ""       // To be used for camera image intent.
    private var currentVideoPath: String = ""       // To be used for camera video intent.

    override fun init() {
        setData()
    }

    fun showPictureOptionsBottomSheet(
            isShowSelectionOptions: Boolean, imagesDirectory: String = "",
            isVideo: Boolean,
    ) {
        val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = (view as ViewGroup).inflate(layoutRes = R.layout.show_picture_options_bottom_sheet)

        view.tvCamera.text = if (isVideo) {
            getString(R.string.take_video)
        } else {
            getString(R.string.take_photo)
        }

        view.tvCamera.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (isVideo) {
                openCameraVideoIntent()
            } else {
                openCameraImageIntent()
            }
        }
        view.tvGallery.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (isVideo) {
                startActivityForResult(ImageUtils.fetchGalleryVideoIntent(), GALLERY_VIDEO_REQUEST)
            } else {
                showPickImagesIntent()
            }
        }
        view.tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    fun showVideoOptionsBottomSheet(
            isShowSelectionOptions: Boolean, imagesDirectory: String = "",
    ) {
        val bottomSheetDialog =
                com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = (view as ViewGroup).inflate(layoutRes = R.layout.show_video_options_bottom_sheet)

        view.tvPickVideo.setOnClickListener {
            bottomSheetDialog.dismiss()
            openCameraVideoIntent()
        }
        view.tvGalleryVideo.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivityForResult(ImageUtils.fetchGalleryVideoIntent(), GALLERY_VIDEO_REQUEST)
        }
        view.tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showPickImagesIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.intent_gallery_picker_title)), GALLERY_IMAGE_REQUEST)
    }

    /**
     * Utility function to open an intent for clicking image via camera.
     */
    private fun openCameraImageIntent() {
        // Get Temporary File and set currentPhoto Path to it.
        ImageUtils.getImageFile(requireContext())?.let { imageFile ->
            currentPhotoPath = imageFile.absolutePath
            startActivityForResult(
                    ImageUtils.fetchCameraImageIntent(imageFile, requireContext()),
                    REQUEST_IMAGE_CAPTURE
            )
        }
    }

    /**
     * Utility function to open an intent for clicking video via camera.
     */
    private fun openCameraVideoIntent() {
        // Get Temporary File and set currentVideo Path to it.
        ImageUtils.getVideoFile(requireContext())?.let { videoFile ->
            currentVideoPath = videoFile.absolutePath
            startActivityForResult(ImageUtils.fetchCameraVideoIntent(videoFile, requireContext()),
                    REQUEST_VIDEO_CAPTURE)
        }
    }


    /**
     * Utility function to handle result of gallery picking intent.
     *
     * @param uri is the uri of the selected image via gallery.
     */
    private fun handleGalleryImage(uri: Uri) {
        // Get Temporary File and copy the URI data to it.
        ImageUtils.getImageFile(requireContext())?.let { imageFile ->
            requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
                viewLifecycleOwner.lifecycleScope.launch {
                    imageFile.copyInputStreamToFile(inputStream)
                }
            }
            // Compress the Image and override the temporary file.
            viewLifecycleOwner.lifecycleScope.launch {
                val path = ImageUtils.compressImageFile(requireContext(), imageFile.absolutePath,
                        Uri.fromFile(imageFile))
                if (path.isNotEmpty()) {
                    onGettingImageFile(File(path))
                }
            }
        }
    }

    /**
     * Utility function to handle result of camera image click intent.
     */
    private fun handleCameraImage() {
        // Compress the Image and override the temporary file.
        val imageFile = File(currentPhotoPath)
        viewLifecycleOwner.lifecycleScope.launch {
            val path = ImageUtils.compressImageFile(requireContext(), imageFile.absolutePath,
                    Uri.fromFile(imageFile))
            if (path.isNotEmpty()) {
                onGettingImageFile(File(path))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                handleGalleryImage(uri)
            }
        }
        // For Image Request the result will be stored at the file path we provided.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            handleCameraImage()
        }
        if (requestCode == GALLERY_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                onGettingVideoUri(uri, ValueMapping.onGalleryVideoType())
            }
        }
        // For Video Request the result will be stored at the file path we provided.
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            val videoFile = File(currentVideoPath)
            onGettingVideoUri(Uri.fromFile(videoFile), ValueMapping.onCaptureVideoType())
        }
    }

    override fun onSampledImageAsyncPostExecute(file: File) {
        onGettingImageFile(file)
    }

    abstract fun setData()

    abstract fun onGettingImageFile(file: File)

    abstract fun onGettingVideoUri(uri: Uri, type: Int)

}