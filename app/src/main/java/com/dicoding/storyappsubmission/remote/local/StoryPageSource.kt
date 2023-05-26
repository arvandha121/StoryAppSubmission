package com.dicoding.storyappsubmission.remote.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyappsubmission.remote.api.ApiService
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory

class StoryPageSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, ListStory>() {
    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val position = params.key ?: PAGE_INDEX
            val dataResponse = apiService.getStory(
                "Bearer $token",
                position,
                params.loadSize,
                1
            ).listStory as List<ListStory>
            LoadResult.Page(
                data = dataResponse,
                prevKey = if (position == PAGE_INDEX) null else position - 1,
                nextKey = if (dataResponse.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val PAGE_INDEX = 1
    }
}