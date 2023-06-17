package com.dicoding.storyappsubmission.remote.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<ListStory>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, ListStory>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}