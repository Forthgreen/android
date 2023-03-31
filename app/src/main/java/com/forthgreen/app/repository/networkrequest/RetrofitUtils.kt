package com.forthgreen.app.repository.networkrequest

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.URLConnection

/**
 * Created by ShrayChona on 03-10-2018.
 */
object RetrofitUtils {

    private val MULTIPART_FORM_DATA = "multipart/form-data"

    fun stringToRequestBody(string: String): RequestBody {
        return string.toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }

    fun imageToRequestBody(file: File): RequestBody {
        return file.asRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }

    fun createPartFromFile(partName: String, file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = file.asRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun createPartFromInputStream(partName: String, inputStream: InputStream): MultipartBody.Part {
        // Try to guess mime type else assign default.
        val mimeType = try {
            URLConnection.guessContentTypeFromStream(inputStream) ?: "image/*"
        } catch (e: Exception) {
            "image/*"
        }
        return MultipartBody.Part.createFormData(partName, "${System.currentTimeMillis()}",
                inputStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull()))
    }

    fun createJsonRequestBody(vararg params: Pair<String, Any>) =
            JSONObject(mapOf(*params)).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

}