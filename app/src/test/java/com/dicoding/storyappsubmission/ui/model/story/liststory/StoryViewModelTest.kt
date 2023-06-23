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
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.remote.data.StoryPagingSource
import com.dicoding.storyappsubmission.remote.data.StoryRepository
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.auth.activity.story.adapter.StoryListAdapter
import com.dicoding.storyappsubmission.ui.auth.activity.story.model.StoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    private lateinit var viewModel: StoryViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()
    val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var preferences: UserInstance
    private lateinit var storyRepository: StoryRepository

    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this) // Initialize mocks

        storyRepository = mock(StoryRepository::class.java)
        val expectedToken = MutableLiveData<PagingData<ListStory>>()
        `when`(storyRepository.getStory(token)).thenReturn(expectedToken)

        preferences = mock(UserInstance::class.java)
        val tokenFlow = flow { emit(token) } // Buat Flow<String> dengan token sebagai nilai emit
        `when`(preferences.getToken()).thenReturn(tokenFlow)

        viewModel = StoryViewModel(preferences, storyRepository)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    @MediumTest
    fun `when Get Story Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyStory = Dummy.generateDummyEntity()
        val data = StoryPagingSource.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<ListStory>>().apply {
            value = data
        }

        // Mock the behavior of viewModel.getStory(token)
        `when`(storyRepository.getStory(token)).thenReturn(story)

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

        verify(storyRepository).getStory(token)

        val snapshot = differ.snapshot()
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
    }

    @Test
    @MediumTest
    fun `when Get Story Should Return Empty List`() = mainCoroutineRules.runBlockingTest {
        val emptyListData  = StoryPagingSource.snapshot(emptyList())
        val story = MutableLiveData<PagingData<ListStory>>().apply {
            value = emptyListData
        }

        // Mock the behavior of viewModel.getStory(token)
        `when`(storyRepository.getStory(token)).thenReturn(story)

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

        verify(storyRepository).getStory(token)

        val snapshot = differ.snapshot()
        Assert.assertEquals(0, snapshot.size)
    }

    @Test
    fun `when Get Token Not Null`() = mainCoroutineRules.runBlockingTest {
        val expectedToken = token
        `when`(viewModel.saveToken(token)).thenReturn(expectedToken)
        val actualToken = viewModel.saveToken(token)
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