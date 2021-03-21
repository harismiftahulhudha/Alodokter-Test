package id.haris.galleryapplication

data class CustomGalleryModel(
    val id: String,
    val path: String,
    var filename: String?,
    var select: Boolean = false
)