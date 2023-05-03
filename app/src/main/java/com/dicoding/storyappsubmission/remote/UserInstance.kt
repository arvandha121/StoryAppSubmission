package com.dicoding.storyappsubmission.remote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserInstance private constructor(private val dataStore: DataStore<Preferences>) {

    private val keyToken = stringPreferencesKey("user_token")

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[keyToken] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[keyToken] = token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserInstance? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserInstance {
            return INSTANCE ?: synchronized(this) {
                val instance = UserInstance(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}