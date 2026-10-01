package com.repairshop.tracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.repairshop.tracker.databinding.ActivityLoginBinding
import com.repairshop.tracker.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dynamically pre-fill username from SharedPreferences if previously saved
        val savedUsername = com.repairshop.tracker.RepairShopApp.prefManager.userName
        if (savedUsername.isNotEmpty()) {
            binding.etUsername.setText(savedUsername)
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text?.toString()?.trim()
            if (username.isNullOrEmpty()) {
                binding.tilUsername.error = "Please enter your username"
                return@setOnClickListener
            }
            binding.tilUsername.error = null

            // Save login state & user info to SharedPreferences
            com.repairshop.tracker.RepairShopApp.prefManager.login(username, isOffline = false)
            Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }

        binding.btnWorkOffline.setOnClickListener {
            val enteredName = binding.etUsername.text?.toString()?.trim()
            val offlineUser = if (!enteredName.isNullOrEmpty()) {
                enteredName
            } else {
                com.repairshop.tracker.RepairShopApp.prefManager.userName.ifEmpty { "Offline Technician" }
            }

            // Save offline login state & user info to SharedPreferences
            com.repairshop.tracker.RepairShopApp.prefManager.login(offlineUser, isOffline = true)
            Toast.makeText(this, "Continuing in Offline Mode as $offlineUser", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
