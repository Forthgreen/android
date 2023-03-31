package com.forthgreen.app.views.dialogfragments

import androidx.appcompat.widget.SearchView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Country
import com.forthgreen.app.views.adapters.CountriesListAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_fragment_country_code.*
import kotlinx.android.synthetic.main.toolbar_dialog_fragments.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*

/**
 * Created by ShrayChona on 03-10-2018.
 * @description this class is used for picking up country codes
 */
class CountryCodesDialogFragment : BaseDialogFragment() {

    companion object {
        const val INTENT_EXTRAS_COUNTRY_CODE = "countryCode"
        const val INTENT_EXTRAS_COUNTRY_NAME = "countryName"
        const val REQUEST_CODE_COUNTRY_CODES_DIALOG_FRAGMENT = 432

        fun newInstance(isShowCountryCode: Boolean): CountryCodesDialogFragment {
            val countryCodesDialogFragment = CountryCodesDialogFragment()
            countryCodesDialogFragment.isShowCountryCode = isShowCountryCode
            return countryCodesDialogFragment
        }
    }

    private var isShowCountryCode: Boolean = true

    override val isFullScreenDialog: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.dialog_fragment_country_code

    override fun init() {
        // set title
        tvToolbarTitle.text = getString(R.string.select_country_code)


        // set recyclerview
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL))
        val mCountriesListAdapter = CountriesListAdapter(this, countriesList, isShowCountryCode)
        recyclerView.adapter = mCountriesListAdapter

        // handle search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                mCountriesListAdapter.searchCountry(newText.trim().toLowerCase())
                return false
            }
        })
    }

    private val countriesList: ArrayList<Country>
        get() {
            val countriesList = ArrayList<Country>()
            val inputStream = resources.openRawResource(R.raw.countries_data)
            try {
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                if (!jsonString.isEmpty()) {
                    val jsonArray = JSONArray(jsonString)
                    val gson = Gson()
                    (0 until jsonArray.length()).mapTo(countriesList) {
                        gson.fromJson(jsonArray.getString(it),
                                Country::class.java)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return countriesList
        }
}