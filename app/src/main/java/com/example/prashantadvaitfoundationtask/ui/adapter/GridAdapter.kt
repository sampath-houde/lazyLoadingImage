package com.example.prashantadvaitfoundationtask.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.prashantadvaitfoundationtask.R
import com.example.prashantadvaitfoundationtask.databinding.GridViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.URL


class GridAdapter(private val context: Context) : BaseAdapter()  {

    private val memoryCache = LruCache<String, Bitmap>(cacheSize())

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var imageUrl = emptyList<String>();

    private fun cacheSize(): Int {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        return (maxMemory / 8).toInt()
    }

    private suspend fun loadBitmap(imageUrl: String, position: Int): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            Timber.i("Loading Image: $position")
            bitmap = BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())
            memoryCache.put(imageUrl, bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bitmap
    }

    override fun getCount(): Int {
        return imageUrl.size
    }

    fun submitList(url: List<String>) {
        imageUrl = url
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return imageUrl[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

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

        coroutineScope.async {
            val bitmap = loadBitmap(imageUrl[position], position)
            bitmap?.let {
                imageView.setImageBitmap(it)
            }
            return@async imageView
        }

        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_launcher_background))
        return imageView
    }

}