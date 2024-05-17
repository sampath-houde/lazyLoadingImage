package com.example.prashantadvaitfoundationtask.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import timber.log.Timber

class DiffUtils : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        Timber.d((oldItem == newItem).toString())
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        Timber.d((oldItem == newItem).toString())
        return oldItem == newItem
    }
}
