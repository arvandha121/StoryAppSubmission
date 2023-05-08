package com.dicoding.storyappsubmission.ui.view.activity.story.model

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.remote.api.ApiConfig
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.remote.response.story.getstory.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val preferences: UserInstance) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStory>>()
    val listStory: LiveData<List<ListStory>> = _listStory

    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.saveToken(token)
        }
    }

    fun getStory(token: String) {
        val client = ApiConfig.getApiService().getStories(("Bearer $token"))
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory as List<ListStory>?
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    class Factory(private val preferences: UserInstance) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                    StoryViewModel(preferences) as T
                }
                else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
            }
        }
    }
}