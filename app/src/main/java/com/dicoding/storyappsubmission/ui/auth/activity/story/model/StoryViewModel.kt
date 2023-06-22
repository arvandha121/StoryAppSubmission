package com.dicoding.storyappsubmission.ui.auth.activity.story.model

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.remote.data.StoryRepository
import com.dicoding.storyappsubmission.remote.di.Injection
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import kotlinx.coroutines.launch

class StoryViewModel(
    private val preferences: UserInstance,
    private val storyRepository: StoryRepository,
) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStory>>()
    val listStory: LiveData<List<ListStory>> = _listStory

    var getToken: LiveData<String> = preferences.getToken().asLiveData()

    fun saveToken(token: String): String {
        viewModelScope.launch {
            preferences.saveToken(token)
        }
        return token
    }

    fun getStory(token: String): LiveData<PagingData<ListStory>> = storyRepository.getStory(token).cachedIn(viewModelScope)

    class Factory(private val preferences: UserInstance, private val context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                    StoryViewModel(preferences, Injection.provideRepository(context)) as T
                }

                else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
            }
        }
    }
}
