package com.example.prashantadvaitfoundationtask.utils

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.collection.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImageLoader {

    private val memoryCache: LruCache<String, Bitmap>
    private val memCacheLog: MutableList<Triple<String, String, String>>
    private val jobMap = mutableMapOf<ImageView, Job>()

    init {
        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = (Runtime.getRuntime().maxMemory() / 1024) / 8
        memoryCache = LruCache<String, Bitmap>(cacheSize.toInt())
        memCacheLog = arrayListOf()
    }

    fun loadImage(url: String, imageView: ImageView, progressBar: ProgressBar, position: Int) {
        cancelPotentialWork(imageView)

        val cachedBitmap = getBitmapFromMemCache(url) ?: DiskCacheUtils.getBitmapFromDiskCache(url)
        if (cachedBitmap != null) {
            CoroutineScope(Dispatchers.Main).launch {
                imageView.setImageBitmap(cachedBitmap)
                progressBar.visibility = View.GONE
            }

        } else {
            imageView.tag = url
            val job = CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapUtils.decodeSampledBitmapFromUrl(url, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT)
                    if (imageView.tag == url && bitmap != null) {
                        withContext(Dispatchers.Main) {
                            imageView.setImageBitmap(bitmap)
                            progressBar.visibility = View.GONE
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            addBitmapToMemoryCache(url, bitmap, position)
                            DiskCacheUtils.putBitmapToDiskCache(url, bitmap)
                        }
                    }
                }
            jobMap[imageView] = job
        }
    }

    fun cancelPotentialWork(imageView: ImageView) {
        jobMap[imageView]?.cancel()
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? {
        return memoryCache.get(key)
    }

    private fun addBitmapToMemoryCache(key: String, bitmap: Bitmap, position: Int) {
        if (getBitmapFromMemCache(key) == null) {
            memCacheLog.add(Triple("Position", "$position", key))
            memoryCache.put(key, bitmap)
        }
    }
}