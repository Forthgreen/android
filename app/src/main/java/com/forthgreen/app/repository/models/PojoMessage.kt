package com.forthgreen.app.repository.models

import androidx.annotation.StringRes

/**
 * Created by ShrayChona on 03-10-2018.
 */
data class PojoMessage(@StringRes val resId: Int? = null, val message: String? = null)