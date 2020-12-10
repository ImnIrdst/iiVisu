package com.imn.ivisusample.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).apply { show() }
}

fun Context.getDrawableCompat(@DrawableRes resId: Int) =
    ContextCompat.getDrawable(this, resId)

