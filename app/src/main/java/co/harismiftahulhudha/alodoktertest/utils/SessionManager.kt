package co.harismiftahulhudha.alodoktertest.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import co.harismiftahulhudha.alodoktertest.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


enum class SortContents { NEWEST, OLDEST }

data class SortPreferences(val sortContents: SortContents, val id: Int, val isUserEdit: Boolean, val isContentScrollToTop: Boolean)

private const val TAG = "SessionManager"

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private val DATASTORE_NAME = "${BuildConfig.APPLICATION_ID}_${SessionManager::class.java.simpleName}_DATASTORE"
    }

    private val dataStore = context.createDataStore(DATASTORE_NAME)

    val preferencesFlow = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
            } else {
                throw it
            }
        }
        .map {
            val sortContents = SortContents.valueOf(it[PreferencesKeys.SORT_CONTENT] ?: SortContents.NEWEST.name)
            val id = it[PreferencesKeys.ID] ?: -1
            val isUserEdit = it[PreferencesKeys.IS_USER_EDIT] ?: false
            val isContentScrollToTop = it[PreferencesKeys.IS_CONTENT_SCROLL_TO_TOP] ?: false
            SortPreferences(sortContents, id, isUserEdit, isContentScrollToTop)
        }

    suspend fun updateSortContents(sortContents: SortContents) {
        dataStore.edit {
            it[PreferencesKeys.SORT_CONTENT] = sortContents.name
        }
    }

    suspend fun updateId(userId: Int = -1) {
        dataStore.edit {
            it[PreferencesKeys.ID] = userId
        }
    }

    suspend fun updateIsUserEdit(isUserEdit: Boolean = false) {
        dataStore.edit {
            it[PreferencesKeys.IS_USER_EDIT] = isUserEdit
        }
    }

    suspend fun updateIsContentScrollToTop(isContentScrollToTop: Boolean = false) {
        dataStore.edit {
            it[PreferencesKeys.IS_CONTENT_SCROLL_TO_TOP] = isContentScrollToTop
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it[PreferencesKeys.SORT_CONTENT] = SortContents.NEWEST.name
            it[PreferencesKeys.ID] = -1
            it[PreferencesKeys.IS_USER_EDIT] = false
            it[PreferencesKeys.IS_CONTENT_SCROLL_TO_TOP] = false
        }
    }

    private object PreferencesKeys {
        val SORT_CONTENT = stringPreferencesKey("${DATASTORE_NAME}_SORT_CONTENT")
        val ID = intPreferencesKey("${DATASTORE_NAME}_ID")
        val IS_USER_EDIT = booleanPreferencesKey("${DATASTORE_NAME}_IS_USER_EDIT")
        val IS_CONTENT_SCROLL_TO_TOP = booleanPreferencesKey("${DATASTORE_NAME}_IS_CONTENT_SCROLL_TO_TOP")
    }
}