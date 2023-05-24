package com.dicoding.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dicoding.storyappsubmission.ui.view.activity.login.model.LoginViewModel
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModel {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock

    private lateinit var viewModel: LoginViewModel
}