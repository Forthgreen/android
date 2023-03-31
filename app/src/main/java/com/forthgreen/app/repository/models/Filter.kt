package com.forthgreen.app.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author shraychona@gmail.com
 * @since 21 May 2020
 */

@Parcelize
data class Filter(
        val id: Int = 0,
        val name: String = "",
        val resourceId: Int = 0,
        var isSelected: Boolean = true,
        val products: List<Product> = listOf()
) : Parcelable
