package com.forthgreen.app.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.AsyncTask
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Created by ShrayChona on 03-10-2018.
 */
class GetSampledImage(private val mFragment: androidx.fragment.app.Fragment) : AsyncTask<String, Void, File>() {

    private var mSampledImageAsyncResp: SampledImageAsyncResp? = null
    private val mMyCustomLoader: MyCustomLoader by lazy { MyCustomLoader(mFragment.context) }

    init {
        mSampledImageAsyncResp = mFragment as SampledImageAsyncResp
    }

    override fun onPreExecute() {
        super.onPreExecute()
//        mMyCustomLoader.showProgressDialog(mFragment.getString(R.string.processing_image))
    }

    override fun doInBackground(vararg params: String): File? {
        try {
            val picturePath = params[0]
            val imageDirectory = params[1]
            val isGalleryImage = java.lang.Boolean.valueOf(params[2])!!
            val reqImageWidth = Integer.parseInt(params[3])
            val exif = ExifInterface(picturePath)
            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(picturePath, options)
            options.inSampleSize = calculateInSampleSize(options, reqImageWidth, reqImageWidth)
            options.inJustDecodeBounds = false
            var imageBitmap: Bitmap? = BitmapFactory.decodeFile(picturePath, options)
            when (orientation) {
                6 -> {
                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    if (null != imageBitmap)
                        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap!!.width, imageBitmap.height,
                                matrix, true)
                }
                8 -> {
                    val matrix = Matrix()
                    matrix.postRotate(270f)
                    if (null != imageBitmap)
                        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap!!.width, imageBitmap.height,
                                matrix, true)
                }
                3 -> {
                    val matrix = Matrix()
                    matrix.postRotate(180f)
                    if (null != imageBitmap)
                        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
                                imageBitmap!!.width, imageBitmap.height,
                                matrix, true)
                }
            }
            if (null != imageBitmap) {
                return getImageFile(imageBitmap, picturePath, imageDirectory, isGalleryImage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(file: File?) {
        super.onPostExecute(file)
//        mMyCustomLoader.dismissProgressDialog()
        if (null != file && null != mSampledImageAsyncResp) {
            mSampledImageAsyncResp!!.onSampledImageAsyncPostExecute(file)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getImageFile(bmp: Bitmap, picturePath: String, imageDirectory: String,
                             isGalleryImage: Boolean): File? {
        try {
            val fOut: OutputStream?
            val file: File = if (isGalleryImage) {
                GeneralFunctions.setUpImageFile(imageDirectory)!!
            } else {
                File(picturePath)
            }
            fOut = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            MediaStore.Images.Media.insertImage(mFragment.activity!!.contentResolver,
                    file.absolutePath, file.name,
                    file.name)
            return file
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    interface SampledImageAsyncResp {
        fun onSampledImageAsyncPostExecute(file: File)
    }
}