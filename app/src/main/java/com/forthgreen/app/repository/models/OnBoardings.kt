package com.forthgreen.app.repository.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * @author Nandita Gandhi
 * @since 10-04-2021
 */
data class OnBoardings(
        @StringRes val title: Int = 0,
        @StringRes val description: Int = 0,
        @DrawableRes val boardingImage: Int = 0,
)