package com.dicoding.storyappsubmission.ui.view.activity.story.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ItemRowUserBinding
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.view.activity.detail.DetailActivity

class StoryListAdapter : PagingDataAdapter<ListStory, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    class MyViewHolder(private val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        private var imgPhoto: ImageView = itemView.findViewById(R.id.img_photo)
        private var tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private var tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        fun bind(data:ListStory) {
            Glide.with(itemView.context).load(data.photoUrl).into(imgPhoto)
            tvUsername.text = data.name
            tvDescription.text = data.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(STORIES, data)
                }

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgPhoto, PHOTO),
                        Pair(tvUsername, NAME),
                        Pair(tvDescription, DESCRIPTION),
                    )
                itemView.context.startActivity(intent,optionsCompat.toBundle())
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }

        const val STORIES = "stories"
        const val PHOTO = "photo"
        const val NAME = "name"
        const val DESCRIPTION = "description"
    }
}