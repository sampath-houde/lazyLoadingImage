package com.example.prashantadvaitfoundationtask.utils

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.scaleDown(reqWidth: Int, reqHeight: Int): Bitmap {
    val width = this.width
    val height = this.height

    val scaleWidth = reqWidth.toFloat() / width
    val scaleHeight = reqHeight.toFloat() / height

    // Calculate the larger scale to maintain aspect ratio
    val scaleFactor = if (scaleWidth > scaleHeight) scaleHeight else scaleWidth

    val matrix = Matrix().apply {
        postScale(scaleFactor, scaleFactor)
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}