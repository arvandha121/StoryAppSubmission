package com.dicoding.storyappsubmission.ui.view.activity.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityDetailBinding
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding
    private lateinit var story: ListStory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        story = intent.getParcelableExtra<ListStory>("stories") as ListStory
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.circleImageView)
        binding.tvUsername.text = story.name
        binding.tvItemDescription.text = story.description
    }
}