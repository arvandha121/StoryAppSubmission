package com.dicoding.storyappsubmission.ui.view.activity.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityStoryBinding
import com.dicoding.storyappsubmission.ui.view.MainActivity
import com.dicoding.storyappsubmission.ui.view.activity.login.LoginActivity

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.story)

        onClicked()
    }

    private fun onClicked() {
        binding.logoutButton.setOnClickListener {
            val intent = Intent(this@StoryActivity, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
        }
    }
}