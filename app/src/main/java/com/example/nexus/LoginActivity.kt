package com.example.nexus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set theme to follow system's dark/light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Set the layout for the login screen
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements
        val signuplink = findViewById<TextView>(R.id.gosignup)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val emailEditText = findViewById<EditText>(R.id.emailtxt)
        val passwordEditText = findViewById<EditText>(R.id.passtxt)
        val loginButton = findViewById<Button>(R.id.signbutt)
        val togglePasswordVisibility = findViewById<ImageView>(R.id.togglePasswordVisibility)
        val forgotPasswordLink = findViewById<TextView>(R.id.forgotpass)  // Reference to "Forgot Password?" link

        // Handle forgot password click
        forgotPasswordLink.setOnClickListener {
            showForgotPasswordDialog()
        }

        // Handle signup navigation
        signuplink.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Toggle password visibility
        togglePasswordVisibility.setOnClickListener {
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.nosee)
            } else {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.see)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
            isPasswordVisible = !isPasswordVisible
        }

        // Handle login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = ProgressBar.VISIBLE
            signInWithEmail(email, password, progressBar)
        }
    }

    private fun signInWithEmail(email: String, password: String, progressBar: ProgressBar) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressBar.visibility = ProgressBar.GONE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Fetch the username from Firebase Realtime Database using the user ID
                        val userId = user.uid
                        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        userRef.child(userId).get().addOnSuccessListener { snapshot ->
                            val username = snapshot.child("username").getValue(String::class.java)
                            if (!username.isNullOrEmpty()) {
                                saveLoginState(username)
                                navigateToMainActivity(username)
                            }
                        }
                    } else {
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleAuthError(task.exception)
                }
            }
    }

    private fun handleAuthError(exception: Exception?) {
        val errorMessage = when {
            exception is FirebaseAuthInvalidCredentialsException -> {
                "Incorrect email or password. Please try again."
            }
            exception is FirebaseAuthInvalidUserException -> {
                "User does not exist. Please check your email."
            }
            else -> {
                "Login failed. Please try again later."
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }


    private fun navigateToMainActivity(username: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", username)  // Pass the username to MainActivity
        startActivity(intent)
        finish()
    }

    private fun saveLoginState(username: String) {
        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("isLoggedIn", true)
            putString("username", username)  // Save the username in sharedPreferences
            apply()
        }
    }

    // Show the Forgot Password Dialog
    private fun showForgotPasswordDialog() {
        val dialog = EditText(this)
        dialog.hint = "Enter your email"
        dialog.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your email address to receive a password reset link.")
            .setView(dialog)
            .setPositiveButton("Send") { _, _ ->
                val email = dialog.text.toString().trim()
                if (email.isNotEmpty()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Send the password reset email using Firebase Authentication
    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent. Please check your inbox.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
