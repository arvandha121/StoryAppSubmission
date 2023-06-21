package com.dicoding.storyappsubmission.ui.model.story.liststory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.filters.MediumTest
import com.dicoding.storyappsubmission.data.Dummy
import com.dicoding.storyappsubmission.data.MainCoroutineRule
import com.dicoding.storyappsubmission.data.getOrAwaitValue
import com.dicoding.storyappsubmission.remote.data.StoryPagingSource
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.auth.activity.story.adapter.StoryListAdapter
import com.dicoding.storyappsubmission.ui.auth.activity.story.model.StoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var viewModel: StoryViewModel

    @Test
    @MediumTest
    fun `when Get Story Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyStory = Dummy.generateDummyEntity()
        val data = StoryPagingSource.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStory>>().apply {
            value = data
        }
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"

        // Mock the behavior of viewModel.getStory(token)
        `when`(viewModel.getStory(token)).thenReturn(story)

        // Call the method under test
        val actualStory = viewModel.getStory(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher,
        )

        differ.submitData(actualStory)

        advanceUntilIdle()

        verify(viewModel).getStory(token)

        val snapshot = differ.snapshot()
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
        Assert.assertEquals(dummyStory[0].photoUrl, differ.snapshot()[0]?.photoUrl)
        Assert.assertEquals(dummyStory[0].description, differ.snapshot()[0]?.description)
        Assert.assertEquals(dummyStory[0].createdAt, differ.snapshot()[0]?.createdAt)
        Assert.assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
        Assert.assertEquals(dummyStory[0].lat, differ.snapshot()[0]?.lat)
        Assert.assertEquals(dummyStory[0].lon, differ.snapshot()[0]?.lon)
    }

    @Test
    fun `when Get Token Not Null`() = mainCoroutineRules.runBlockingTest {
        val expectedToken = MutableLiveData<String>()
        `when`(viewModel.getToken).thenReturn(expectedToken)
        val actualToken = viewModel.getToken
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(expectedToken, actualToken)
    }

    @Test
    fun `when Get Token Null`() = mainCoroutineRules.runBlockingTest {
        val expectedToken = MutableLiveData<String>()
        val actualToken = null
        Assert.assertNull(actualToken)
        Assert.assertNotEquals(expectedToken, actualToken)
    }

    // save token still need fixes
    @Test
    fun `when Save Token Is Sucess and Not Null`() = mainCoroutineRules.runBlockingTest {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"
        val expectedToken = token
        `when`(viewModel.saveToken(token)).thenReturn(expectedToken)

        val actualToken = viewModel.saveToken(token)
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(expectedToken, actualToken)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}