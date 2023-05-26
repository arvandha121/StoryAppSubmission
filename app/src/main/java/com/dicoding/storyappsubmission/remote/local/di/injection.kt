//package com.dicoding.storyappsubmission.remote.local.di
//
//import android.content.Context
//import com.dicoding.storyappsubmission.remote.api.ApiConfig
//import com.dicoding.storyappsubmission.remote.local.data.StoryRepository
//
//object injection {
//    fun provideRepository(context: Context): StoryRepository {
//        val database = StoryRepository.getDatabase(context)
//        val apiService = ApiConfig.getApiService()
//        return StoryRepository(database, apiService)
//    }
//}