package com.dicoding.storyappsubmission.remote.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyappsubmission.remote.api.ApiService
import com.dicoding.storyappsubmission.remote.database.StoryDatabase
import com.dicoding.storyappsubmission.remote.database.StoryRemoteMediator
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory(token: String): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "bearer $token"),
            pagingSourceFactory = {
//                StoryPagingSource(token, apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getLocation(token: String): List<ListStory> {
        return apiService.getStories("bearer $token").listStory as List<ListStory>
    }
}