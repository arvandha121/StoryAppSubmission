package com.dicoding.storyappsubmission.ui.view.activity.login.model

import androidx.lifecycle.*
import com.dicoding.storyappsubmission.remote.UserInstance
import kotlinx.coroutines.launch

class LoginViewModel (private val preferences: UserInstance) : ViewModel() {
    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.saveToken(token)
        }
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