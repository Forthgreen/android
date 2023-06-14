package com.forthgreen.app.utils

import android.os.Environment
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter

/**
 * Created by ShrayChona on 03-10-2018.
 */
object Constants {

    const val APP_NAME = "Forthgreen"

    const val CONTENT_TYPE = "application/json"

    //    const val COUNTRY_CODE = "+91"
    const val DEVICE_TYPE = "android"

    const val MAX_LINES_LIMIT = 3

    // Media Constants
    const val LOCAL_FILE_PREFIX = "file://"
    private val LOCAL_STORAGE_BASE_PATH_FOR_MEDIA = Environment
            .getExternalStorageDirectory().toString() + "/" + APP_NAME
    val LOCAL_STORAGE_BASE_PATH_FOR_USER_MEDIA = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/User/Media/"
    val LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/User/Photos/"
    val LOCAL_STORAGE_BASE_PATH_FOR_USER_POST_PHOTOS = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/User/Post/"
    val LOCAL_STORAGE_BASE_PATH_FOR_USER_POST_VIDEOS = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/User/Videos/"
    val LOCAL_STORAGE_BASE_PATH_FOR_USER_AUDIO = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/User/Audios/"

    val categoriesList = arrayListOf(Filter(1, "Clothing", R.drawable.ic_category_cloth),
            Filter(2, "Beauty", R.drawable.ic_category_beauty),
            Filter(7, "Accessories", R.drawable.ic_category_accessories),
            Filter(4, "Food", R.drawable.ic_category_food),
            Filter(5, "Drinks", R.drawable.ic_category_drinks),
            Filter(3, "Health", R.drawable.ic_category_health),
            Filter(6, "Miscellaneous", R.drawable.ic_category_misc))

    val restaurantFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Gluten-Free", R.drawable.ic_filter_gluten_free))
        add(Filter(3, "Organic", R.drawable.ic_filter_organic))
        add(Filter(5, "Pizza", R.drawable.ic_filter_pizza))
        add(Filter(7, "Bakery", R.drawable.ic_filter_bakery))
        add(Filter(9, "Pub", R.drawable.ic_filter_pub))
        add(Filter(2, "Fast food", R.drawable.ic_filter_fast_food))
        add(Filter(4, "Juice bar", R.drawable.ic_filter_juice_bar))
        add(Filter(6, "Macrobiotic", R.drawable.ic_filter_macro_bio))
        add(Filter(8, "Salad bar", R.drawable.ic_filter_salad_bar))
        add(Filter(10, "Take out", R.drawable.ic_filter_take_out))
    }

    val clothingWomenFilterList = arrayListOf<Filter>().apply {
        add(Filter(4, "Tops"))
        add(Filter(3, "Bottoms"))
        add(Filter(5, "Trainers & Shoes"))
        add(Filter(1, "Activewear"))
        add(Filter(2, "Jackets and Coats"))
    }

    val clothingMenFilterList = arrayListOf<Filter>().apply {
        add(Filter(9, "Tops"))
        add(Filter(8, "Bottoms"))
        add(Filter(10, "Trainers & Shoes"))
        add(Filter(6, "Activewear"))
        add(Filter(7, "Jackets and Coats"))
    }

    val beautyWomenFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Makeup"))
        add(Filter(2, "Haircare"))
        add(Filter(3, "Face"))
        add(Filter(4, "Body"))
        add(Filter(5, "Other"))
    }

    val beautyMenFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Makeup"))
        add(Filter(2, "Haircare"))
        add(Filter(3, "Face"))
        add(Filter(4, "Body"))
        add(Filter(5, "Other"))
    }

    val accessoriesWomenFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Watches"))
        add(Filter(2, "Backpacks"))
        add(Filter(3, "Bags & Purses"))
        add(Filter(4, "Jewelery"))
        add(Filter(5, "Other"))
    }

    val accessoriesMenFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Watches"))
        add(Filter(2, "Backpacks"))
        add(Filter(3, "Bags & Purses"))
        add(Filter(4, "Jewelery"))
        add(Filter(5, "Other"))
    }

    val foodFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Cheese"))
        add(Filter(2, "Proteins"))
        add(Filter(3, "Sauces"))
        add(Filter(4, "Bars"))
        add(Filter(5, "Other"))
    }

    val drinksFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Beer"))
        add(Filter(2, "Wine"))
        add(Filter(3, "Soft drinks"))
        add(Filter(4, "Spirits"))
        add(Filter(5, "Other"))
    }

    val healthFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Supplements"))
        add(Filter(2, "Sanitiser"))
        add(Filter(3, "Other"))
    }

    val miscFilterList = arrayListOf<Filter>().apply {
        add(Filter(1, "Candles"))
        add(Filter(2, "Aromatic oils"))
        add(Filter(3, "Other"))
    }
}