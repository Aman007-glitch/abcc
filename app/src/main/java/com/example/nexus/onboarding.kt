package com.example.nexus

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
        setContentView(R.layout.onboard_slidng) // Ensure this layout has a fragment container

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        // Show the first onboarding fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, slide11()) // Replace with the first slide
                .commit()
        }
    }

    fun replaceFragment(fragment: Fragment, position: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null) // Add to backstack for navigation
            .commit()
    }

    // Method to be called when onboarding is completed
    fun finishOnboarding() {
        // Save the onboarding completion status to SharedPreferences
        val editor = prefs.edit()
        editor.putBoolean(KEY_FIRST_TIME_USER, false)
        editor.apply() // Save changes asynchronously

        // Navigate to LoginActivity after completing onboarding
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close the onboarding activity
    }
}
