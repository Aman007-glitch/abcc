package com.example.nexus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        usernameTextView = findViewById(R.id.usernameTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Retrieve username from SharedPreferences
        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "User")  // Default to "User" if not found

        // Set the username in the TextView
        usernameTextView.text = "Welcome, $username"

        // Handle logout button click
        logoutButton.setOnClickListener {
            logout()
        }
    }

    // Function to log out the user
    private fun logout() {
        // Clear login state from SharedPreferences
        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("isLoggedIn", false) // Set login state to false
        editor.apply()

        // Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut()

        // Redirect to the login screen after logging out
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close ProfileActivity so the user can't navigate back to it
    }
}
