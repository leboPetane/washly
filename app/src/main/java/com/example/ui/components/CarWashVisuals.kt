package com.example.ui.components

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Returns a real photograph of the mobile car wash in action for each car wash provider.
 */
@DrawableRes
fun getCarWashDrawable(carWashId: Long): Int {
    return when (carWashId) {
        1L -> R.drawable.real_carwash_sparkle
        2L -> R.drawable.real_carwash_aquaglide
        3L -> R.drawable.real_carwash_apex
        else -> R.drawable.real_carwash_sparkle
    }
}
