package com.dicoding.storyappsubmission.ui.view.activity.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityLogin2Binding
import com.dicoding.storyappsubmission.ui.custome.EditTextEmail
import com.dicoding.storyappsubmission.ui.custome.EditTextPassword
import com.dicoding.storyappsubmission.ui.view.MainActivity
import com.dicoding.storyappsubmission.ui.view.activity.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogin2Binding

    private lateinit var editTextEmail: EditTextEmail
    private lateinit var editTextPassword: EditTextPassword
    private var isEmail: Boolean = false
    private var isPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        if (!intent.getStringExtra("email").isNullOrEmpty()) {
            editTextEmail.setText(intent.getStringExtra("email"))
            isEmail = true
        }
        if (!intent.getStringExtra("password").isNullOrEmpty()) {
            editTextPassword.setText(intent.getStringExtra("password"))
            isPassword = true
        }

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
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