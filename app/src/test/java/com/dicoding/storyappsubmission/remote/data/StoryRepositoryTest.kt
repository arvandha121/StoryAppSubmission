package com.dicoding.storyappsubmission.remote.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.dicoding.storyappsubmission.data.Dummy
import com.dicoding.storyappsubmission.data.MainCoroutineRule
import com.dicoding.storyappsubmission.data.getOrAwaitValue
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.auth.activity.story.adapter.StoryListAdapter
import com.dicoding.storyappsubmission.ui.model.story.liststory.noopListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Location Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyLocation = Dummy.generateDummyEntity()
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"
        Mockito.`when`(storyRepository.getLocation(token)).thenReturn(dummyLocation)

        val actualLocation = storyRepository.getLocation(token)
        assertNotNull(actualLocation)
        assertEquals(dummyLocation, actualLocation)
    }

    @Test
    fun `when Get Stories Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyStory = Dummy.generateDummyEntity()
        val data = StoryPagingSource.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStory>>().apply {
            value = data
        }
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"

        Mockito.`when`(storyRepository.getStory(token)).thenReturn(story)
        val actualStory = storyRepository.getStory(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher,
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        Mockito.verify(storyRepository).getStory(token)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
        assertEquals(dummyStory[0].photoUrl, differ.snapshot()[0]?.photoUrl)
        assertEquals(dummyStory[0].description, differ.snapshot()[0]?.description)
        assertEquals(dummyStory[0].createdAt, differ.snapshot()[0]?.createdAt)
        assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
        assertEquals(dummyStory[0].lat, differ.snapshot()[0]?.lat)
        assertEquals(dummyStory[0].lon, differ.snapshot()[0]?.lon)
    }

}