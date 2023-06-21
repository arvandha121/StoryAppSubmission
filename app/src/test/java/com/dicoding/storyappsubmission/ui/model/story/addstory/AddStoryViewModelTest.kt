package com.dicoding.storyappsubmission.ui.model.story.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyappsubmission.data.MainCoroutineRule
import com.dicoding.storyappsubmission.ui.auth.activity.story.model.AddStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var addStoryViewModel: AddStoryViewModel

    @Test
    fun `when Get Add Story Not Null and Return Data`() = mainCoroutineRules.runBlockingTest {
        val expectedStory = MutableLiveData<String>()
        Mockito.`when`(addStoryViewModel.getToken).thenReturn(expectedStory)

        val actualToken = addStoryViewModel.getToken
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(expectedStory, actualToken)
    }

    @Test
    fun `when Get Token Null`() = mainCoroutineRules.runBlockingTest {
        val expectedToken = MutableLiveData<String>()
        val actualToken = null

        Assert.assertNull(actualToken)
        Assert.assertNotEquals(expectedToken, actualToken)
    }
}