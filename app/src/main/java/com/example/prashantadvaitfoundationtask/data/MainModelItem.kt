package com.example.prashantadvaitfoundationtask.data

data class MainModelItem(
    val backupDetails: BackupDetails,
    val coverageURL: String,
    val id: String,
    val language: String,
    val mediaType: Int,
    val publishedAt: String,
    val publishedBy: String,
    val thumbnail: Thumbnail,
    val title: String
) {
    fun getThumbnailUrl() = thumbnail.domain + "/" + thumbnail.basePath + "/0/" + thumbnail.key
}

data class Thumbnail(
    val aspectRatio: Double,
    val basePath: String,
    val domain: String,
    val id: String,
    val key: String,
    val qualities: List<Int>,
    val version: Int
)
data class BackupDetails(
    val pdfLink: String,
    val screenshotURL: String
)

