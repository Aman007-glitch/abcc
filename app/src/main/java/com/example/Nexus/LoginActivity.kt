package com.example.nexus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set theme to follow system's dark/light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Set the layout for the login screen
        setContentView(R.layout.activity_login)

        // Initialize UI elements
        val emailEditText = findViewById<EditText>(R.id.emailtxt)
        val passwordEditText = findViewById<EditText>(R.id.passtxt)
        val loginButton = findViewById<Button>(R.id.signbutt)

        // Initialize Appwrite Client
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1") // Your Appwrite API endpoint
            .setProject("677e9f3b001989c63249") // Your Appwrite project ID
            .setSelfSigned(true) // Remove in production if using a valid certificate

        val account = Account(client)

        // Set onClickListener for Login Button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim() // Trim to remove extra spaces
            val password = passwordEditText.text.toString().trim()

            // Validate input fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Launch the login/register process inside a coroutine
            lifecycleScope.launch {
                try {
                    // Attempt to log in using the email and password
                    try {
                        // Log in the user
                        account.createSession(email, password) // Logs in the user if credentials are correct
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                            // After successful login, navigate to the next screen (MainActivity)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()  // Close the LoginActivity
                        }
                    } catch (e: AppwriteException) {
                        // If login fails (user doesn't exist), create a new user
                        if (e.message?.contains("User not found") == true) {
                            try {
                                // User doesn't exist, create a new user using email as the user ID
                                val user=account.create(
                                    ID.unique(),// creates unique userid
                                    email,         // Email from input field
                                    password       // Password from input field
                                )

                                // After successful registration, log the user in
                                account.createSession(email, password)

                                runOnUiThread {
                                    Toast.makeText(this@LoginActivity, "Registration successful!", Toast.LENGTH_SHORT)
                                        .show()

                                    // After successful login, navigate to the next screen (MainActivity)
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()  // Close the LoginActivity
                                }

                            } catch (e: AppwriteException) {
                                // Handle registration errors
                                runOnUiThread {
                                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // If it's another error, show the error message
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle any unforeseen errors
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "An unexpected error occurred.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
