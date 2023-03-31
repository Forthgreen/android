package com.forthgreen.app.views.fragments

import android.os.Bundle
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.utils.loadURL
import kotlinx.android.synthetic.main.fragment_image.*

/**
 * @author shraychona@gmail.com
 * @since 03 Jun 2020
 */
class ImageFragment : BaseFragment() {
    companion object {

        const val BUNDLE_EXTRAS_IMAGE_PATH = "imagePath"

        fun newInstance(image: String): ImageFragment {
            val imageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_EXTRAS_IMAGE_PATH, image)
            imageFragment.arguments = bundle
            return imageFragment
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_image

    override fun init() {
        // get arguments
        if (null != arguments) {
            val image = arguments!!.getString(BUNDLE_EXTRAS_IMAGE_PATH, "")

            // check if image is a local file or url is missing for now
            ivImage.loadURL(image, false)
//            else if (image.path.isNotEmpty()) {
//                val imageFile = File(image.path)
//                if (imageFile.exists()) {
//                    setImage(GeneralFunctions.getLocalImageUri(imageFile))
//                }
//            }
        }
    }

    override val viewModel: BaseViewModel?
        get() = null

}