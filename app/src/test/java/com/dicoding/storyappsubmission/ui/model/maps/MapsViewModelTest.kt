package com.dicoding.storyappsubmission.ui.model.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyappsubmission.data.Dummy
import com.dicoding.storyappsubmission.data.MainCoroutineRule
import com.dicoding.storyappsubmission.data.getOrAwaitValue
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.auth.activity.maps.model.MapsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var mapsViewModel: MapsViewModel

    @Test
    fun `when Get Token Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val expectedMaps = MutableLiveData<String>()
        Mockito.`when`(mapsViewModel.getToken).thenReturn(expectedMaps)

        val actualMaps = mapsViewModel.getToken
        assertNotNull(actualMaps)
        assertEquals(expectedMaps, actualMaps)
    }

    @Test
    fun `when Get Token Null`() = mainCoroutineRules.runBlockingTest {
        val expectedMaps = MutableLiveData<String>()
        val actualMaps = null

        assertNull(actualMaps)
        assertNotEquals(expectedMaps, actualMaps)
    }

    @Test
    fun `when Get Location Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyMapsStory = Dummy.generateDummyEntity()
        val mapsStory = MutableLiveData<List<ListStory>>().apply {
            value = dummyMapsStory
        }

        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"
        mapsViewModel.getLocation(token)

        Mockito.`when`(mapsViewModel.listStory).thenReturn(mapsStory)

        val actualStory = mapsViewModel.listStory.getOrAwaitValue()
        assertNotNull(actualStory)
        assertEquals(dummyMapsStory.size, actualStory.size)
        assertEquals(dummyMapsStory, actualStory)
    }
}