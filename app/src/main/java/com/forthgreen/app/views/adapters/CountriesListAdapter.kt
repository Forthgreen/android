package com.forthgreen.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Country
import com.forthgreen.app.views.dialogfragments.CountryCodesDialogFragment
import com.forthgreen.app.views.dialogfragments.CountryCodesDialogFragment.Companion.INTENT_EXTRAS_COUNTRY_CODE
import com.forthgreen.app.views.dialogfragments.CountryCodesDialogFragment.Companion.INTENT_EXTRAS_COUNTRY_NAME
import kotlinx.android.synthetic.main.row_country.view.*

/**
 * Created by ShrayChona on 02-11-2018.
 */
class CountriesListAdapter(private val mCountryCodesDialogFragment: CountryCodesDialogFragment,
                           private val countriesList: ArrayList<Country>,
                           private val isShowCountryCode: Boolean) :
        androidx.recyclerview.widget.RecyclerView.Adapter<CountriesListAdapter.CountryCodeViewHolder>() {

    private val originalCountryCodesList = ArrayList<Country>()
    private val mLayoutInflater: LayoutInflater

    init {
        originalCountryCodesList.addAll(countriesList)
        mLayoutInflater = LayoutInflater.from(mCountryCodesDialogFragment.activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCodeViewHolder =
            CountryCodeViewHolder(mLayoutInflater.inflate(R.layout.row_country,
                    parent, false))

    override fun onBindViewHolder(countryCodeViewHolder: CountryCodeViewHolder, position: Int) {
        countryCodeViewHolder.bindCountry(countriesList[position])
    }

    override fun getItemCount(): Int = countriesList.size

    fun searchCountry(searchedKeyword: String) {
        countriesList.clear()
        if (searchedKeyword.isEmpty()) {
            countriesList.addAll(originalCountryCodesList)
        } else {
            originalCountryCodesList.filterTo(countriesList) {
                it.name.toLowerCase()
                        .contains(searchedKeyword)
            }
        }
        notifyDataSetChanged()
    }

    inner class CountryCodeViewHolder(private val containerView: View) : androidx.recyclerview.widget.RecyclerView
    .ViewHolder(containerView) {

        fun bindCountry(country: Country) {
            with(country) {
                containerView.tvCountryName.text = name
                containerView.tvCountryCode.text = dial_code
                containerView.tvCountryCode.visibility = if (isShowCountryCode) View.VISIBLE else View.GONE
                containerView.llParent.setOnClickListener {
                    mCountryCodesDialogFragment.dismiss()
                    if (null != mCountryCodesDialogFragment.targetFragment) {
                        mCountryCodesDialogFragment.targetFragment!!
                                .onActivityResult(mCountryCodesDialogFragment
                                        .targetRequestCode, android.app.Activity.RESULT_OK,
                                        android.content.Intent().putExtra(INTENT_EXTRAS_COUNTRY_CODE,
                                                countriesList[adapterPosition]
                                                        .dial_code).putExtra(INTENT_EXTRAS_COUNTRY_NAME, countriesList[adapterPosition].name))
                    }
                }
            }
        }
    }

}