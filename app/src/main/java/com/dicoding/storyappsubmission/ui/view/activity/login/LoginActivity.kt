package com.dicoding.storyappsubmission.ui.view.activity.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.dicoding.storyappsubmission.ui.custome.ButtonLogin
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
    private lateinit var loginButton: ButtonLogin

    private lateinit var viewModel: LoginViewModel

    private var isEmail: Boolean = false
    private var isPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.EditTextEmail
        password = binding.EditTextPassword
        loginButton = binding.loginButton

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

    private fun loginButtonEnable() {
        loginButton.isEnabled = isEmail && isPassword
        loginButton.isEnabled = isEmail && isPassword
    }

    private fun onClicked() {
        viewModel = ViewModelProvider(
            this@LoginActivity,
            LoginViewModel.Factory(preferences = UserInstance.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginButtonEnable()
        textEmail()
        textPassword()

        loginButton.setOnClickListener {
            val client = ApiConfig.getApiService().login(
                email.text.toString(),
                password.text.toString()
            )
            client.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        if (responseBody.error == true) {
                            Toast.makeText(
                                this@LoginActivity,
                                responseBody.message,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            save(responseBody.login as Login)
                            Toast.makeText(
                                this@LoginActivity,
                                "login "+responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.message(),
                            Toast.LENGTH_LONG
                        ).show()
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

    private fun textEmail() {
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.isEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isEmail = !s.isNullOrEmpty() && emailRegex.matches(s.toString())
                loginButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun textPassword() {
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.isEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPassword = !s.isNullOrEmpty() && passwordRegex.matches(s.toString())
                loginButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
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

    companion object {
        const val EMAIL = "email"
        const val PASSWORD = "password"
        val emailRegex: Regex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
        val passwordRegex: Regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}\$")

    }
}