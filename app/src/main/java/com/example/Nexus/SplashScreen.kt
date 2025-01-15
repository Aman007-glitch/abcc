package com.example.nexus

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_FIRST_TIME_USER = "isFirstTimeUser"
    }

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_splash_screen)

        // Set up window insets for padding adjustment
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            // Get the value of isFirstTimeUser from SharedPreferences
            val isFirstTimeUser = prefs.getBoolean(KEY_FIRST_TIME_USER, true)

            val intent = if (isFirstTimeUser) {
                // If it's the user's first time, navigate to onboarding
                Intent(this, onboarding::class.java)
            } else {
                // If not the first time, navigate to login
                Intent(this, LoginActivity::class.java)
            }

            startActivity(intent)
            finish() // Close the splash screen activity to prevent back navigation
        }, 3000) // Splash screen delay for 3 seconds
    }
}
