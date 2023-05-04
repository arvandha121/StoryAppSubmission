package com.dicoding.storyappsubmission.ui.view.activity.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityLogin2Binding
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.remote.api.ApiConfig
import com.dicoding.storyappsubmission.remote.response.login.Login
import com.dicoding.storyappsubmission.remote.response.login.LoginResponse
import com.dicoding.storyappsubmission.ui.custome.EditTextEmail
import com.dicoding.storyappsubmission.ui.custome.EditTextPassword
import com.dicoding.storyappsubmission.ui.view.MainActivity
import com.dicoding.storyappsubmission.ui.view.activity.login.model.LoginViewModel
import com.dicoding.storyappsubmission.ui.view.activity.register.RegisterActivity
import com.dicoding.storyappsubmission.ui.view.activity.story.StoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogin2Binding

    private lateinit var email: EditTextEmail
    private lateinit var password: EditTextPassword

    private lateinit var viewModel: LoginViewModel

    private var isEmail: Boolean = false
    private var isPassword: Boolean = false

    companion object {
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val TAG = "password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.EditTextEmail
        password = binding.EditTextPassword

        setupView()

        if (!intent.getStringExtra(EMAIL).isNullOrEmpty()) {
            email.setText(intent.getStringExtra(EMAIL))
            isEmail = true
        }
        if (!intent.getStringExtra(PASSWORD).isNullOrEmpty()) {
            password.setText(intent.getStringExtra(PASSWORD))
            isPassword = true
        }

        onClicked()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun onClicked() {
        viewModel = ViewModelProvider(
            this@LoginActivity,
            LoginViewModel.Factory(preferences = UserInstance.getInstance(dataStore))
        )[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val client = ApiConfig.getApiService().login(
                email.text.toString(),
                password.text.toString()
            )
            client.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        if (responseBody.error == true) {
                            Toast.makeText(this@LoginActivity, responseBody.message, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            save(responseBody.login as Login)
                            Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                        Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                }

            })
        }

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun save(login: Login) {
        viewModel.saveToken(login.token as String)
        val intent = Intent(this@LoginActivity, StoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}