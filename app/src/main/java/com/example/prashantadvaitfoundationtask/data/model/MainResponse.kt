package com.example.prashantadvaitfoundationtask.data.model

data class MainResponse(
    val backupDetails: BackupDetails ?= null,
    val coverageURL: String ?= null,
    val id: String ?= null,
    val language: String ?= null,
    val mediaType: Int ?= null,
    val publishedAt: String ?= null,
    val publishedBy: String ?= null,
    val thumbnail: Thumbnail ?= null,
    val title: String ?= null
) {
    fun getThumbnailUrl() = thumbnail?.domain + "/" + thumbnail?.basePath + "/0/" + thumbnail?.key
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

