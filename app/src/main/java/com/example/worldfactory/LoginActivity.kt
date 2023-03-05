package com.example.worldfactory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isEmpty
import com.example.worldfactory.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.buttonSignup.setOnClickListener {
            if (binding.textInputLayoutName.editText?.text.toString().isNullOrEmpty() || binding.textInputLayoutEmail.editText?.text.toString().isNullOrEmpty() || binding.textInputLayoutPassword.editText?.text.toString().isNullOrEmpty()){
              //Can't sign up!
            } else {
                val intent = Intent(this@LoginActivity, WordActivity::class.java)
                startActivity(intent)
            }
        }
    }
}