package net.techandgraphics.wastical.data.local

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

@Singleton class Preferences @Inject constructor(@ApplicationContext val context: Context) {

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

  suspend inline fun <reified T> get(key: String, default: T): T {
    return when (default) {
      is Int -> context.dataStore.data.first()[intPreferencesKey(key)] ?: default
      is Boolean -> context.dataStore.data.first()[booleanPreferencesKey(key)] ?: default
      is Long -> context.dataStore.data.first()[longPreferencesKey(key)] ?: default
      is Float -> context.dataStore.data.first()[floatPreferencesKey(key)] ?: default
      else -> context.dataStore.data.first()[stringPreferencesKey(key)] ?: default
    } as T
  }

  inline fun <reified T> flowOf(key: String, default: T): Flow<T> {
    return when (default) {
      is Int -> context.dataStore.data.map { (it[intPreferencesKey(key)] ?: default) as T }
      is Boolean -> context.dataStore.data.map { (it[booleanPreferencesKey(key)] ?: default) as T }
      is Long -> context.dataStore.data.map { (it[longPreferencesKey(key)] ?: default) as T }
      is Float -> context.dataStore.data.map { (it[floatPreferencesKey(key)] ?: default) as T }
      is String -> context.dataStore.data.map { (it[stringPreferencesKey(key)] ?: default) as T }
      else -> throw IllegalArgumentException("Unsupported type")
    }
  }

  companion object {
    const val PREFS_NAME = "quantcal_prefs"
    const val CURRENT_WORKING_MONTH = "current_working_month"
    const val DYNAMIC_COLOR = "dynamic_color"
    const val FCM_TOKEN_KEY = "fcm_token_key"
    const val LOGIN_SUCCESS = "login_success"
  }
}
