package com.dicoding.storyappsubmission.ui.view.activity.maps.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.remote.data.StoryRepository
import com.dicoding.storyappsubmission.remote.di.Injection
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.view.activity.story.model.StoryViewModel
import kotlinx.coroutines.launch

class MapsViewModel(
    private val preferences: UserInstance,
    private val storyRepository: StoryRepository,
) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStory>>()
    var listStory: LiveData<List<ListStory>> = _listStory

    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun getLocation(token: String) {
        viewModelScope.launch {
            _listStory.postValue(storyRepository.getLocation(token))
        }
    }

    class Factory(private val preferences: UserInstance, private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                    MapsViewModel(preferences, Injection.provideRepository(context)) as T
                }

                else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
            }
        }
    }

}