package com.example.prashantadvaitfoundationtask.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridLayout
import android.widget.GridView
import android.widget.ImageView
import com.example.prashantadvaitfoundationtask.databinding.GridViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.URL

class ImageAdapter(private val context: Context, val imageUrls: List<String>) : BaseAdapter() {
    private val memoryCache = LruCache<String, Bitmap>(cacheSize())
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val loadingTasks = mutableMapOf<Int, Job>()
    private var imageUrl = emptyList<String>();

    init {
        this.imageUrl = imageUrls
    }

    override fun getCount(): Int = imageUrl.size

    override fun getItem(position: Int): Any = imageUrl[position]

    override fun getItemId(position: Int): Long = position.toLong()
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            imageView = convertView as ImageView
        }
        loadImage(position, imageView)
        return imageView
    }

    fun submitList(list: List<String>) {
        imageUrl = list
        notifyDataSetChanged()
    }

    private fun loadImage(position: Int, imageView: ImageView) {
        val imageUrl = imageUrl[position]
        val bitmap = memoryCache.get(imageUrl)

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            Timber.i("Loading position: $position")
            // Cancel previous loading task if any
            loadingTasks[position]?.cancel()
            // Start new loading task
            val task = coroutineScope.launch {
                val loadedBitmap = loadBitmap(imageUrl)
                loadedBitmap?.let {
                    if (imageView.tag == position) {
                        imageView.setImageBitmap(it)
                    }
                }
            }
            loadingTasks[position] = task
        }
    }

    private suspend fun loadBitmap(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            bitmap = memoryCache.get(imageUrl)
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())
                memoryCache.put(imageUrl, bitmap)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bitmap
    }

    private fun cacheSize(): Int {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        return (maxMemory / 8).toInt()
    }

    fun cancelAllTasks() {
        coroutineScope.coroutineContext.cancelChildren()
        loadingTasks.clear()
    }
}