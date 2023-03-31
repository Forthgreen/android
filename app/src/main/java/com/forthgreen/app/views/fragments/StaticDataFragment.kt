package com.forthgreen.app.views.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.forthgreen.app.R
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.viewmodels.StaticDataViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.android.synthetic.main.fragment_static_data.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Chetan Tuteja on 06-Jun-20.
 */
class StaticDataFragment : BaseFragment() {

    companion object {
        const val TAG = "StaticDataFragment"
        private const val BUNDLE_EXTRAS_STATIC_DATA_TYPE = "TypeOfStaticDataViewed"

        fun newInstance(mStaticDataType: Int = ValueMapping.onStaticDataPrivacyPolicy()): StaticDataFragment {
            return StaticDataFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_EXTRAS_STATIC_DATA_TYPE, mStaticDataType)
                }
            }
        }
    }

    //Variables
    private val mStaticDataType by lazy { arguments?.getInt(BUNDLE_EXTRAS_STATIC_DATA_TYPE, ValueMapping.onStaticDataPrivacyPolicy())!! }

    //Markdown library instance to display markDown post content.
    private val mMarkdownManager by lazy {
        Markwon.builder(requireContext())
                .usePlugin(CorePlugin.create())
                .usePlugin(HtmlPlugin.create())
                .usePlugin(GlideImagesPlugin.create(requireContext()))
                .build()
    }

    private val mStaticDataViewModel by lazy {
        //Getting ViewModel
        ViewModelProvider(this).get(StaticDataViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_static_data

    override val viewModel: BaseViewModel?
        get() = mStaticDataViewModel

    override fun init() {
        setupViews()
        observeProperties()
    }

    private fun setupViews() {
        //Fetch Static Data
        mStaticDataViewModel.fetchStaticData()

        //Setup Toolbar title according to the type
        when (mStaticDataType) {
            ValueMapping.onStaticDataAboutUs() -> {
                tvToolbarTitle.text = getString(R.string.about_us_label)
            }
            ValueMapping.onStaticDataPrivacyPolicy() -> {
                tvToolbarTitle.text = getString(R.string.privacy_label)
            }
            ValueMapping.onStaticDataTerms() -> {
                tvToolbarTitle.text = getString(R.string.terms_amp_conditions_label)
            }
        }
    }

    //Helper method to observe data via ViewModel
    private fun observeProperties() {
        mStaticDataViewModel.onStaticDataReceived().observe(viewLifecycleOwner, Observer { staticData ->
            showProgressDialog(false)
            //Load Data According to the type
            when (mStaticDataType) {
                ValueMapping.onStaticDataAboutUs() -> {
                    mMarkdownManager.setMarkdown(tvStaticData, staticData.data.aboutUs)
                }
                ValueMapping.onStaticDataPrivacyPolicy() -> {
                    mMarkdownManager.setMarkdown(tvStaticData, staticData.data.privacyPolicy)
                }
                ValueMapping.onStaticDataTerms() -> {
                    mMarkdownManager.setMarkdown(tvStaticData, staticData.data.termsAndCondition)
                }
            }
        })
    }

}
