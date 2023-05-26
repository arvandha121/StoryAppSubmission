package com.dicoding.storyappsubmission.remote.local.data

import com.dicoding.storyappsubmission.remote.api.ApiService
import com.dicoding.storyappsubmission.remote.local.database.StoryDatabase
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.remote.response.story.getstory.StoryResponse

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    suspend fun getStory(token: String): List<ListStory> {
        return apiService.getStory("Bearer $token", 1, 10, 1).listStory as List<ListStory>
    }
}