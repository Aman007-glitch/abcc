package com.example.Nexus

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class onboarding : AppCompatActivity() {

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_FIRST_TIME_USER = "isFirstTimeUser"
    }

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val isFirstTimeUser = prefs.getBoolean(KEY_FIRST_TIME_USER, true)

        setContentView(R.layout.onboard_slidng) // Ensure this layout has a valid container for fragments

        if (savedInstanceState == null && isFirstTimeUser) {
            // Start with the first onboarding slide
            replaceFragment(slide11(), false)
        }
    }

    fun replaceFragment(fragment: Fragment, isLastSlide: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .apply {
                if (!isLastSlide) {
                    addToBackStack(null) // Allow back navigation only if not the last slide
                }
            }
            .commit()

        if (isLastSlide) {
            markOnboardingComplete()
            navigateToLogin()
        }
    }

    private fun markOnboardingComplete() {
        prefs.edit()
            .putBoolean(KEY_FIRST_TIME_USER, false)
            .apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close OnboardingActivity to prevent back navigation
    }
}
