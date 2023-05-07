package com.dicoding.storyappsubmission.ui.view.activity.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.view.activity.detail.DetailActivity

class StoryAdapter(private val listStories: ArrayList<ListStory>) : RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imgPhoto: ImageView = itemView.findViewById(R.id.img_photo)
        private var tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private var tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: ListStory) {
            tvUsername.text = story.name
            tvDescription.text = story.description
            Glide.with(itemView.context).load(story.photoUrl).into(imgPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(STORIES, story)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStories[position])
    }

    override fun getItemCount(): Int  = listStories.size

    companion object {
        const val STORIES = "stories"
        const val PHOTO = "photo"
        const val NAME = "name"
        const val DESCRIPTION = "description"
    }
}