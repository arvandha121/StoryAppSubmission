package com.dicoding.storyappsubmission.ui.view.activity.story.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.dicoding.storyappsubmission.remote.UserInstance

class AddStoryViewModel(private val preferences: UserInstance) : ViewModel() {

    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    class Factory(private val preferences: UserInstance) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                    AddStoryViewModel(preferences) as T
                }
                else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
            }
        }
    }
}