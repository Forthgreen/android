package com.forthgreen.app.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forthgreen.app.repository.models.Filter

/**
 * @author Nandita Gandhi
 * @since 12-08-2021
 */
class ProductsFiltersViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val TAG = "CategoryFiltersViewModel"
    }

    private var onAllFiltersSelected = MutableLiveData<Boolean>()
    private val selectAllFilters = MutableLiveData<Boolean>()

    fun checkSelectedFilters(filterList: List<Filter>) {
        onAllFiltersSelected.value = filterList.all { it.isSelected }
    }

    fun selectAllFilters(select: Boolean) {
        selectAllFilters.value = select
    }

    fun onFilterChanged(): LiveData<Boolean> = onAllFiltersSelected
    fun onSelectAllFilters(): LiveData<Boolean> = selectAllFilters
}