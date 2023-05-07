package com.dicoding.storyappsubmission.ui.view.activity.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}