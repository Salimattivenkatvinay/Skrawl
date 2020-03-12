package com.vinay.skrawl

data class Result(
    val batchcomplete: Boolean,
    val `continue`: Continue,
    val query: Query
)

data class Continue(
    val `continue`: String,
    val gpsoffset: Int
)

data class Query(
    val pages: List<Page>,
    val redirects: List<Redirect>
)

data class Page(
    val index: Int,
    val ns: Int,
    val pageid: Int,
    val terms: Terms,
    val thumbnail: Thumbnail,
    val title: String
)

data class Terms(
    val description: List<String>
)

data class Thumbnail(
    val height: Int,
    val source: String,
    val width: Int
)

data class Redirect(
    val from: String,
    val index: Int,
    val to: String
)