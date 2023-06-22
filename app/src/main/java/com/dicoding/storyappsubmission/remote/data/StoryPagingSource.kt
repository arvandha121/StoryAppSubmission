package com.dicoding.storyappsubmission.remote.data

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyappsubmission.remote.api.ApiService
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory

class StoryPagingSource(
    private val preferences: String,
    private val apiService: ApiService
) : PagingSource<Int, ListStory>() {
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = preferences
            Log.d("Token", "bearer $token")
            val responseData = apiService.getStory(
                "bearer $token",
                position,
                params.loadSize,
                0
            ).listStory as List<ListStory>
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        fun snapshot(items: List<ListStory>): PagingData<ListStory> {
            return PagingData.from(items)
        }
        const val INITIAL_PAGE_INDEX = 1
    }

}