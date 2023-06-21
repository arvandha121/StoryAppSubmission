package com.dicoding.storyappsubmission.ui.model.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyappsubmission.data.Dummy
import com.dicoding.storyappsubmission.data.MainCoroutineRule
import com.dicoding.storyappsubmission.remote.response.login.LoginResponse
import com.dicoding.storyappsubmission.ui.auth.activity.login.model.LoginViewModelContract
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
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var loginViewModelContract: LoginViewModelContract
    private val dummyLogin = Dummy.generateDummyLogin()
    private val login = MutableLiveData<LoginResponse>()

    @Test
    fun `when Save Token Is Success and Not Null`() = mainCoroutineRules.runBlockingTest {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"
        val expectedToken = token
        Mockito.`when`(loginViewModelContract.saveToken(token)).thenReturn(expectedToken)
        val actualToken = loginViewModelContract.saveToken(token)
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(expectedToken, actualToken)
    }

    @Test
    fun `when Save Token Is Success With Dummy and Not Null`() = mainCoroutineRules.runBlockingTest {
        login.value = dummyLogin
    }
}