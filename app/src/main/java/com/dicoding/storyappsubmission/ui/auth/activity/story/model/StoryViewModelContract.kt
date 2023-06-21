package com.dicoding.storyappsubmission.ui.auth.activity.story.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import kotlinx.coroutines.launch

interface StoryViewModelContract {
    fun getStory(token: String): LiveData<PagingData<ListStory>>
    fun saveToken(token: String): String
}