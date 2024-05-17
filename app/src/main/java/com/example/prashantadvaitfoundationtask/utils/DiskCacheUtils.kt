package com.example.prashantadvaitfoundationtask.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jakewharton.disklrucache.DiskLruCache
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object DiskCacheUtils {

    private const val DISK_CACHE_SIZE = 1024 * 1024 * 50 // 50MB
    private const val DISK_CACHE_SUBDIR = "thumbnails"
    private var diskLruCache: DiskLruCache? = null

    fun init(context: Context) {
        val cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        diskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE.toLong())
    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        val cachePath =
            if (context.externalCacheDir != null && context.externalCacheDir?.canWrite()!!) {
                context.externalCacheDir?.path
            } else {
                context.cacheDir.path
            }
        return File(cachePath + File.separator + uniqueName)
    }

    fun putBitmapToDiskCache(key: String, bitmap: Bitmap) {
        val hashKey = hashKeyForDisk(key)
        var outputStream: OutputStream? = null
        try {
            val editor = diskLruCache?.edit(hashKey)
            if (editor != null) {
                outputStream = editor.newOutputStream(0)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                editor.commit()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }

    fun getBitmapFromDiskCache(key: String): Bitmap? {
        val hashKey = hashKeyForDisk(key)
        var bitmap: Bitmap? = null
        try {
            val snapshot = diskLruCache?.get(hashKey)
            if (snapshot != null) {
                val inputStream = snapshot.getInputStream(0)
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun hashKeyForDisk(key: String): String {
        return try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray())
            bytesToHexString(mDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            key.hashCode().toString()
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }
}