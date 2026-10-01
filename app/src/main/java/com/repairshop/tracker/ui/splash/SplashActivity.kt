package com.repairshop.tracker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.repairshop.tracker.databinding.ActivitySplashBinding
import com.repairshop.tracker.ui.auth.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Delay 1.2s then dynamically navigate based on SharedPreferences session
        Handler(Looper.getMainLooper()).postDelayed({
            val targetActivity = if (com.repairshop.tracker.RepairShopApp.prefManager.isLoggedIn) {
                com.repairshop.tracker.ui.main.MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, targetActivity))
            finish()
        }, 1200)
    }
}
