package com.dicoding.storyappsubmission.remote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class Auth(private val dataStore: DataStore<Preferences>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking {
            dataStore.data.first()[stringPreferencesKey("token")]
        }
        val process = if (!token.isNullOrEmpty()) {
            val authorized = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
                chain.proceed(authorized)
            } else {
                chain.proceed(original)
            }

        return process
    }
}