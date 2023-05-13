package com.dicoding.storyappsubmission.ui.view.activity.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.ui.view.MainActivity
import com.dicoding.storyappsubmission.ui.view.activity.story.StoryActivity
import com.dicoding.storyappsubmission.ui.view.activity.story.dataStore
import com.dicoding.storyappsubmission.ui.view.activity.story.model.StoryViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        viewModel()
    }

    private fun viewModel() {
        val preferences = UserInstance.getInstance(dataStore)

        viewModel = ViewModelProvider(
            this,
            StoryViewModel.Factory(preferences)
        )[StoryViewModel::class.java]

        viewModel.getToken().observe(this) {
            if (it.isNullOrEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                }, 3000)
            }
            else {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(
                        Intent(this, StoryActivity::class.java)
                    )
                    finish()
                }, 3000)
            }
        }
    }
}