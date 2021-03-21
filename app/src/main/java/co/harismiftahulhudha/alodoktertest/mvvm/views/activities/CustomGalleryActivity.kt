package co.harismiftahulhudha.alodoktertest.mvvm.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.alodoktertest.BuildConfig
import co.harismiftahulhudha.alodoktertest.R
import co.harismiftahulhudha.alodoktertest.helpers.GridSpacingItemDecorationHelper
import co.harismiftahulhudha.alodoktertest.mvvm.viewmodels.CustomGalleryFolderViewModel
import co.harismiftahulhudha.alodoktertest.mvvm.views.adapters.CustomGalleryAdapter
import dagger.hilt.android.AndroidEntryPoint
import id.haris.galleryapplication.CustomGalleryFolderModel
import id.haris.galleryapplication.CustomGalleryModel

@AndroidEntryPoint
class CustomGalleryActivity : AppCompatActivity() {
    private val customGalleryFolderViewModel: CustomGalleryFolderViewModel by viewModels()
    private lateinit var spinner: Spinner
    private lateinit var btnSelect: TextView
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var folders: List<CustomGalleryFolderModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomGalleryAdapter
    private lateinit var selectedFiles: MutableList<String>

    companion object {
        val FILE = "${BuildConfig.APPLICATION_ID}_${CustomGalleryActivity::class.java.simpleName}_file"
        val FILE_NAME = "${BuildConfig.APPLICATION_ID}_${CustomGalleryActivity::class.java.simpleName}_file_name"
        val FILES = "${BuildConfig.APPLICATION_ID}_${CustomGalleryActivity::class.java.simpleName}_files"
        val IS_MULTIPLE = "${BuildConfig.APPLICATION_ID}_${CustomGalleryActivity::class.java.simpleName}_is_multiple"
    }

    private var isMultiple = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_gallery)

        isMultiple = intent.getBooleanExtra(IS_MULTIPLE, false)

        initComponents()
        subscribeObservers()
        subscribeListener()

        customGalleryFolderViewModel.getFolders()
    }

    private fun initComponents() {
        selectedFiles = mutableListOf()
        folders = ArrayList()
        val folder: List<String> = ArrayList()
        spinnerAdapter = ArrayAdapter(this, R.layout.custom_item_gallery_spinner_selected, folder)
        spinnerAdapter.setDropDownViewResource(R.layout.custom_item_spinner_dropdown)
        spinner = findViewById(R.id.customGallerySpinner)
        spinner.adapter = spinnerAdapter

        btnSelect = findViewById(R.id.customGallerySelect)
        recyclerView = findViewById(R.id.customGalleryRecycler)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.addItemDecoration(GridSpacingItemDecorationHelper(3, 5, true))
        adapter = CustomGalleryAdapter()
        recyclerView.adapter = adapter

        if (isMultiple) {
            btnSelect.visibility = View.VISIBLE
        } else {
            btnSelect.visibility = View.GONE
        }
    }

    private fun subscribeObservers() {
        customGalleryFolderViewModel.files().observe(this, { files ->
            if (files != null) {
                adapter.setModel(files)
                adapter.notifyDataSetChanged()
                if (BuildConfig.DEBUG) {
                    files.forEach {
                        Log.d("LOG_FILES", "subscribeObservers: ${it.path} === ${it.filename}")
                    }
                }
            }
        })

        customGalleryFolderViewModel.folders().observe(this, { folders ->
            if (folders != null) {
                this.folders = folders
            }
        })

        customGalleryFolderViewModel.folderNames().observe(this, { folders ->
            if (folders != null) {
                spinnerAdapter.addAll(folders)
                spinnerAdapter.notifyDataSetChanged()

            }
        })
    }

    private fun subscribeListener() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val folder = CustomGalleryFolderModel("-1", spinner.getItemAtPosition(position).toString(), 3)
                val index = folders.indexOf(folder)
                val data = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> folders[index].id
                    else -> folders[index].title
                }
                customGalleryFolderViewModel.getFiles(data, selectedFiles)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        adapter.setOnClickListener(object : CustomGalleryAdapter.OnClickListener {
            override fun onClick(position: Int, model: CustomGalleryModel) {
                if (isMultiple) {
                    val select = when {
                        model.select -> false
                        else -> true
                    }
                    if (select) {
                        if (selectedFiles.size == 10) {
                            Toast.makeText(
                                this@CustomGalleryActivity,
                                "Maksimal 10 File",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            model.select = select
                            selectedFiles.add(model.path)
                            adapter.updateModel(position, model)
                        }
                    } else {
                        model.select = select
                        selectedFiles.remove(model.path)
                        adapter.updateModel(position, model)
                    }
                    val text = "Pilih (${selectedFiles.size})"
                    btnSelect.text = text
                } else {
                    val returnIntent = Intent()
                    returnIntent.putExtra(FILE, model.path)
                    returnIntent.putExtra(FILE_NAME, model.filename)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
        })
    }

    fun back(view: View) {
        onBackPressed()
    }

    fun selectFiles(view: View) {
        if (selectedFiles.size > 0) {
            val returnIntent = Intent()
            returnIntent.putStringArrayListExtra(FILES, selectedFiles as ArrayList<String>)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else {
            Toast.makeText(this, "Minimal harus memilih 1 file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        super.onBackPressed()
    }
}