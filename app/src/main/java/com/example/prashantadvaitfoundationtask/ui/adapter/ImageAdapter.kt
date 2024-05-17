package com.example.prashantadvaitfoundationtask.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prashantadvaitfoundationtask.databinding.GridViewBinding
import com.example.prashantadvaitfoundationtask.ui.MainActivity
import com.example.prashantadvaitfoundationtask.utils.ImageLoader
import timber.log.Timber

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.GridViewHolder>() {

    private val imageUrls = ArrayList<String>()
    inner class GridViewHolder(private val binding: GridViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String, position: Int) {
            ImageLoader.loadImage(imageUrl, binding.imageView, binding.progressBar, position)
        }

        fun onViewRecycled() {
            ImageLoader.cancelPotentialWork(binding.imageView)
            binding.imageView.setImageDrawable(null)
            binding.progressBar.visibility = View.GONE
        }
    }

    fun addImageUrls(newImageUrls: Collection<String>) {
        val startPosition = imageUrls.size+1
        imageUrls.addAll(newImageUrls)
        notifyItemRangeInserted(startPosition, newImageUrls.size)
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
         val binding = GridViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
         return GridViewHolder(binding)
     }

     override fun getItemCount(): Int {
         return imageUrls.size
     }

    override fun onViewRecycled(holder: GridViewHolder) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

     override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
         Timber.i("Binding at position: $position")
         holder.bind(imageUrls[position], position)
     }


 }