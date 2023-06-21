package com.dicoding.storyappsubmission.ui.auth.activity.login.model

interface LoginViewModelContract {
    fun saveToken(token: String): String
}