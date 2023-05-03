package com.dicoding.storyappsubmission.ui.view.activity.register

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityRegisterBinding
import com.dicoding.storyappsubmission.remote.api.ApiConfig
import com.dicoding.storyappsubmission.remote.response.register.RegisterResponse
import com.dicoding.storyappsubmission.ui.custome.EditTextEmail
import com.dicoding.storyappsubmission.ui.custome.EditTextPassword
import com.dicoding.storyappsubmission.ui.view.MainActivity
import com.dicoding.storyappsubmission.ui.view.activity.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var email: EditTextEmail
    private lateinit var password: EditTextPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.EditTextEmail
        password = binding.EditTextPassword

        setupView()

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
        binding.registerButton.setOnClickListener {
            onClickCallback()
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun onClickCallback() {
        val client = ApiConfig.getApiService().register(
            binding.EditTextName.text.toString(),
            email.text.toString(),
            password.text.toString()
        )
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    if (responseBody.error == true) {
                        Toast.makeText(
                            this@RegisterActivity,
                            responseBody.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))

                        Toast.makeText(
                            this@RegisterActivity,
                            responseBody.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                    Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_LONG).show()
            }

        })
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