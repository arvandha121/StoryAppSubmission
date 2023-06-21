package com.dicoding.storyappsubmission.ui.auth.activity.login.model

import androidx.lifecycle.*
import com.dicoding.storyappsubmission.remote.UserInstance
import kotlinx.coroutines.launch

class LoginViewModel (private val preferences: UserInstance) : ViewModel(), LoginViewModelContract {

    override fun saveToken(token: String): String {
        viewModelScope.launch {
            preferences.saveToken(token)
        }
        return token
    }

    class Factory(private val preferences: UserInstance) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(preferences) as T
                }
                else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
            }
        }
    }
}