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
        private const val PREF_NAME = "userPrefs"
        private const val KEY_FIRST_TIME_USER = "isFirstTimeUser"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
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

        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if the user is opening the app for the first time
            val isFirstTimeUser = prefs.getBoolean(KEY_FIRST_TIME_USER, true)
            val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

            val intent = when {
                isFirstTimeUser -> {
                    // Navigate to OnboardingActivity if first time
                    prefs.edit().putBoolean(KEY_FIRST_TIME_USER, false).apply() // Set first time user flag to false
                    Intent(this, onboarding::class.java)
                }
                isLoggedIn -> {
                    // Navigate to MainActivity if the user is logged in
                    Intent(this, MainActivity::class.java)
                }
                else -> {
                    // Navigate to LoginActivity if the user is not logged in
                    Intent(this, LoginActivity::class.java)
                }
            }

            startActivity(intent)
            finish() // Close the splash screen activity
        }, 3000) // Delay for 3 seconds
    }
}
