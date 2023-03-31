package com.forthgreen.app.views.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.forthgreen.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 17-Sep-21
 */
object ImageUtils {

    private const val CHILD_DIR = "/images/"
    private const val TAG = "ImageUtils"


    /**
     * Utility function to return an intent to pick images via gallery.
     */
    fun fetchGalleryImageIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    /**
     * Utility function to return an intent to pick video via gallery.
     */
    fun fetchGalleryVideoIntent(): Intent {
        return Intent().apply {
            type = "video/*"
            action = Intent.ACTION_PICK
        }
    }

    /**
     * Utility function to capture a photo using file provider.
     *
     * @param file is the file where the camera app should save the photo.
     * @param context is the context of wherever the function is being called.
     */
    fun fetchCameraImageIntent(file: File, context: Context): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, getMediaUri(file, context))
        }
    }


    /**
     * Utility function to capture a video using file provider.
     *
     * @param file is the file where the camera app should save the photo.
     * @param context is the context of wherever the function is being called.
     */
    fun fetchCameraVideoIntent(file: File, context: Context): Intent {
        return Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, getMediaUri(file, context))
        }
    }

    /**
     * Utility function return the URI with the give file location for camera intent.
     *
     * @param file is the file where the camera app should save the photo.
     * @param context is the context of wherever the function is being called.
     */
    private fun getMediaUri(file: File, context: Context): Uri {
        return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
        )
    }

    /**
     * Utility function to create a temporary file in the cache directory of the app.
     *
     * @param context is the context of wherever the function is being called.
     */
    fun getImageFile(context: Context?): File? {
        if (context != null) {
            // We create a folder in cache dir, and specify the file.
            val folder = File(context.cacheDir, CHILD_DIR)
            folder.mkdirs()

            val file = File(folder, "JPEG_${System.currentTimeMillis()}.jpg")
            if (file.exists())
                file.delete()
            file.createNewFile()
            return file
        }
        return null
    }

    /**
     * Utility function to create a temporary file in the cache directory of the app.
     *
     * @param context is the context of wherever the function is being called.
     */
    fun getVideoFile(context: Context?): File? {
        if (context != null) {
            // We create a folder in cache dir, and specify the file.
            val folder = File(context.cacheDir, CHILD_DIR)
            folder.mkdirs()

            val file = File(folder, "VID_${System.currentTimeMillis()}.mp4")
            if (file.exists())
                file.delete()
            file.createNewFile()
            return file
        }
        return null
    }

    /**
     * Utility function for compressing the image at given path and also overriding it if necessary.
     *
     * @param context is the context of wherever the function is being called.
     * @param path is the current path of file
     * @param uri is the URI of the current file.
     * @param shouldOverride indicates whether to override and replace the file at current path.
     */
    suspend fun compressImageFile(
            context: Context, path: String, uri: Uri,
            shouldOverride: Boolean = true,
    ): String {
        return withContext(Dispatchers.IO) {
            var scaledBitmap: Bitmap? = null

            try {
                val (hgt, wdt) = ScalingUtils.getImageHgtWdt(uri)
                try {
                    val bm = getBitmapFromUri(context, uri)
                    Log.d(TAG, "original bitmap height${bm?.height} width${bm?.width}")
                    Log.d(TAG, "Dynamic height$hgt width$wdt")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Part 1: Decode image
                val unscaledBitmap = ScalingUtils.decodeFile(context, uri, wdt, hgt,
                        ScalingUtils.ScalingLogic.FIT)
                if (unscaledBitmap != null) {
                    if (!(unscaledBitmap.width <= 800 && unscaledBitmap.height <= 800)) {
                        // Part 2: Scale image
                        scaledBitmap = ScalingUtils.createScaledBitmap(unscaledBitmap, wdt, hgt,
                                ScalingUtils.ScalingLogic.FIT)
                    } else {
                        scaledBitmap = unscaledBitmap
                    }
                }

                val exif: ExifInterface
                try {
                    exif = ExifInterface(path)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED)
                    val matrix = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    }
                    scaledBitmap?.let { sbm ->
                        scaledBitmap = Bitmap.createBitmap(sbm, 0, 0,
                                sbm.width, sbm.height, matrix, true)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                // Store to tmp file
                val mFolder = File(context.cacheDir, CHILD_DIR)
                if (!mFolder.exists()) {
                    mFolder.mkdir()
                }

                val tmpFile = File(mFolder.absolutePath, "IMG_${System.currentTimeMillis()}.png")

                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(tmpFile)
                    scaledBitmap?.compress(
                            Bitmap.CompressFormat.PNG,
                            ScalingUtils.getImageQualityPercent(tmpFile),
                            fos
                    )
                    fos.flush()
                    fos.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                var compressedPath = ""
                if (tmpFile.exists() && tmpFile.length() > 0) {
                    compressedPath = tmpFile.absolutePath
                    if (shouldOverride) {
                        val srcFile = File(path)
                        val result = tmpFile.copyTo(srcFile, true)
                        Log.d(TAG, "copied file ${result.absolutePath}")
                        Log.d(TAG, "Delete temp file ${tmpFile.delete()}")
                    }
                }

                scaledBitmap?.recycle()

                return@withContext if (shouldOverride) path else compressedPath
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            return@withContext ""
        }
    }

    @Throws(IOException::class)
    fun getBitmapFromUri(context: Context, uri: Uri, options: BitmapFactory.Options? = null): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image: Bitmap? = if (options != null)
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        else
            BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }
}

/**
 * Utility function to copy the give input stream into the current file.
 *
 * @param inputStream is the stream that is to be copied.
 */
suspend fun File.copyInputStreamToFile(inputStream: InputStream) {
    withContext(Dispatchers.IO) {
        this@copyInputStreamToFile.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
}