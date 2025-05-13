package net.calmwalija.calnect.shared.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs @Inject constructor(@ApplicationContext val context: Context) {

  val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

  suspend inline fun <reified T> put(key: String, value: T) {
    context.dataStore.edit {
      when (value) {
        is Int -> it[intPreferencesKey(key)] = value
        is String -> it[stringPreferencesKey(key)] = value
        is Boolean -> it[booleanPreferencesKey(key)] = value
        is Long -> it[longPreferencesKey(key)] = value
        is Float -> it[floatPreferencesKey(key)] = value
      }
    }
  }

  suspend fun get(key: String, default: String = ""): String {
    return context.dataStore.data.first()[stringPreferencesKey(key)] ?: default
  }

  fun getAsFlow(key: String): Flow<String?> {
    return context.dataStore.data.map { it[stringPreferencesKey(key)] }
  }

  companion object {
    const val PREFS_NAME = "calnect_prefs"
  }
}
