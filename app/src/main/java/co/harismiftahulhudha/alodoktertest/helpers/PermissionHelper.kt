package co.harismiftahulhudha.alodoktertest.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.harismiftahulhudha.alodoktertest.helpers.RequestCodeHelper.REQUEST_STORAGE

class PermissionHelper(private val context: Context, private val fragment: Fragment) {

    fun isGreaterThenLollipop(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun hasReadAndWriteStoragePermission(): Boolean {
        if (!isGreaterThenLollipop()) return true
        val bol: Boolean = (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
        return bol
    }

    fun requestReadAndWriteStoragePermission() {
        if (!isGreaterThenLollipop()) return
        if (hasReadAndWriteStoragePermission()) return
        fragment.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_STORAGE
        )
    }
}