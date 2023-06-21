package com.dicoding.storyappsubmission.ui.auth.activity.story

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityStoryBinding
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.ui.auth.activity.story.adapter.StoryListAdapter
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import com.dicoding.storyappsubmission.ui.auth.MainActivity
import com.dicoding.storyappsubmission.ui.auth.activity.login.LoginActivity
import com.dicoding.storyappsubmission.ui.auth.activity.maps.MapsActivity
import com.dicoding.storyappsubmission.ui.auth.activity.story.adapter.LoadingAdapter
import com.dicoding.storyappsubmission.ui.auth.activity.story.model.StoryViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    private lateinit var viewModel: StoryViewModel
    private lateinit var listStory: ArrayList<ListStory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.story)

        viewModel()
        onClicked()
    }

    private fun viewModel() {
        val preferences = UserInstance.getInstance(dataStore)

        viewModel = ViewModelProvider(
            this@StoryActivity,
            StoryViewModel.Factory(preferences, this)
        )[StoryViewModel::class.java]

        viewModel.getToken.observe(this) { it ->
            if (it.isEmpty()) {
                startActivity(Intent(this@StoryActivity, LoginActivity::class.java))
                finish()
            } else {
                binding.storyActivity.layoutManager = LinearLayoutManager(this)
                val adapter = StoryListAdapter()
                binding.storyActivity.adapter = adapter.withLoadStateFooter(
                    footer = LoadingAdapter {
                        adapter.retry()
                    }
                )

                viewModel.getStory(it).observe(this) {
                    adapter.submitData(lifecycle, it)
                }
            }
        }

        viewModel.listStory.observe(this) {
            recyclerView(it)
        }
    }

    private fun recyclerView(listStories: List<ListStory>) {
        binding.storyActivity.setHasFixedSize(true)

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            binding.storyActivity.layoutManager = GridLayoutManager(this@StoryActivity, 1)
        else
            binding.storyActivity.layoutManager = GridLayoutManager(this@StoryActivity, 1)

        addAllListStory(listStories)
        adapter()
    }

    private fun addAllListStory(listStories: List<ListStory>) {
        listStory = ArrayList()
        listStory.addAll(listStories)
    }

    private fun adapter() {
        val adapter = StoryListAdapter()
        binding.storyActivity.adapter = adapter
    }

    private fun maps() {
        startActivity(Intent(this@StoryActivity, MapsActivity::class.java))
    }

    private fun logout() {
        val dialogView = LayoutInflater
            .from(this)
            .inflate(R.layout.logout_popup, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(getString(R.string.title_confirm))

        val alertDialog = dialogBuilder.show()

        dialogView.findViewById<Button>(R.id.btn_logout_yes).setOnClickListener {
            // Tindakan saat tombol "Ya" ditekan
            alertDialog.dismiss()

            //Tambahkan kode untuk logout disini
            val sharedPref = getSharedPreferences(MY_APP_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val token = viewModel.saveToken("")
            Intent(this@StoryActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
                editor.remove(token.toString())
                editor.apply()
            }
            Toast.makeText(this@StoryActivity, LOGOUT, Toast.LENGTH_SHORT).show()
        }

        dialogView.findViewById<Button>(R.id.btn_logout_no).setOnClickListener {
            // Tindakan saat tombol "Tidak" ditekan
            alertDialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mapButton -> {
                maps()
                return true
            }
            R.id.logoutButton -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onClicked() {
        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    override fun onBackPressed() {
        logout()
    }

    companion object {
        const val MY_APP_PREFS = "my_app_prefs"
        const val LOGOUT = "logout success"
    }
}