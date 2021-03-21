package co.harismiftahulhudha.alodoktertest.mvvm.repositories

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import id.haris.galleryapplication.CustomGalleryFolderModel
import id.haris.galleryapplication.CustomGalleryModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

@Singleton
class CustomGalleryRepository @Inject constructor(
    private val context: Context
): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val files: MutableLiveData<MutableList<CustomGalleryModel>> = MutableLiveData()
    val folders: MutableLiveData<MutableList<CustomGalleryFolderModel>> = MutableLiveData()
    val folderNames: MutableLiveData<MutableList<String>> = MutableLiveData()

    val folderTitles: MutableList<String> = mutableListOf()

    @SuppressLint("Recycle")
    suspend fun getFiles(bucket: String, selectedFiles: MutableList<String> = mutableListOf()) {
        val file: MutableList<CustomGalleryModel> = ArrayList()
        GlobalScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uri = MediaStore.Files.getContentUri("external")
                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.BUCKET_ID,
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.SIZE
                )

                val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE} AND ${MediaStore.Files.FileColumns.BUCKET_ID}='${bucket}'"

                context.contentResolver.query(
                    uri,
                    projection,
                    selection,
                    null,
                    "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
                ).run {
                    val columnIndexId =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val columnIndexSize =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val columnIndeDisplayName =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val columnIndexData =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    while (this?.moveToNext()!!) {
                        val id = this.getLong(columnIndexId!!)
                        val path = ContentUris.withAppendedId(uri, id)
                        var name = this.getString(columnIndeDisplayName!!)
                        if (name.isNullOrEmpty()) {
                            val split = path.toString().split("/")
                            name = split[split.size - 1]
                        }
                        val select = when {
                            selectedFiles.size > 0 && selectedFiles.indexOf(path.toString()) != -1 -> {
                                true
                            }
                            else -> {
                                false
                            }
                        }
                        val customGallery = CustomGalleryModel(
                            id.toString(),
                            path.toString(),
                            name,
                            select
                        )
                        file.add(customGallery)
                    }
                    this.close()
                }

                files.postValue(file)
            } else {
                val uri = MediaStore.Files.getContentUri("external")
                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.PARENT,
                    MediaStore.Files.FileColumns.DISPLAY_NAME
                )

                val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE} AND ${MediaStore.Files.FileColumns.DATA} LIKE '%${bucket}%'"

                context.contentResolver.query(
                    uri,
                    projection,
                    selection,
                    null,
                    "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
                ).run {
                    val columnIndexId =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val columnIndexData =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    val columnIndexDisplayName =
                        this?.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)

                    while (this?.moveToNext()!!) {
                        val id = this.getLong(columnIndexId!!)
                        val data = this.getString(columnIndexData!!)
                        var name = this.getString(columnIndexDisplayName!!)
                        val path = "file://${data}"
                        if (name.isNullOrEmpty()) {
                            val split = path.split("/")
                            name = split[split.size - 1]
                        }
                        val select = when {
                            selectedFiles.size > 0 && selectedFiles.indexOf(path) != -1 -> {
                                true
                            }
                            else -> {
                                false
                            }
                        }
                        val customGallery = CustomGalleryModel(
                            id.toString(),
                            path,
                            name,
                            select
                        )
                        file.add(customGallery)
                    }
                    this.close()
                }

                files.postValue(file)
            }
        }
    }

    suspend fun getFolders() {
        withContext(Dispatchers.IO) {
            folderTitles.clear()

            val fileFolders: MutableList<CustomGalleryFolderModel> = ArrayList()
            fileFolders.addAll(getFolderImage())
            Collections.sort(folderTitles)

            folders.postValue(fileFolders)
            folderNames.postValue(folderTitles)
        }
    }

    @SuppressLint("Recycle")
    private fun getFolderImage(): MutableList<CustomGalleryFolderModel> {
        val fileFolders: MutableList<CustomGalleryFolderModel> = ArrayList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            context.contentResolver.query(
                uriExternal,
                projection,
                null,
                null,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC"
            ).run {

                val indexBucketId = this?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val indexBucketName =
                    this?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (this?.moveToNext()!!) {
                    val bucketId = this.getLong(indexBucketId!!)
                    val bucketName = this.getString(indexBucketName!!)

                    if (folderTitles.indexOf(bucketName) == -1) {
                        val folder = CustomGalleryFolderModel(bucketId.toString(), bucketName, 0)
                        fileFolders.add(folder)
                        folderTitles.add(bucketName)
                    }
                }
                this.close()
            }
            return fileFolders
        } else {
            val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            )
            val orderBy = MediaStore.Images.Media.DATE_TAKEN

            context.contentResolver.query(uriExternal, projection, null, null, orderBy).run {
                val columnIndexBucketId =
                    this?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val columnIndexBucketName =
                    this?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val columnIndexData = this?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                while (this?.moveToNext()!!) {
                    val bucketId = this.getString(columnIndexBucketId!!)
                    val bucket = this.getString(columnIndexBucketName!!)
                    val data = this.getString(columnIndexData!!)
                    if (folderTitles.indexOf(bucket) == -1) {
                        val split = data.split("/")
                        val folder = CustomGalleryFolderModel(bucketId, bucket, (split.size - 1))
                        fileFolders.add(folder)
                        folderTitles.add(bucket)
                    }
                }
                this.close()
            }

            return fileFolders
        }
    }
}