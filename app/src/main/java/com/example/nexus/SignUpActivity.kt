package com.example.nexus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var resendLinkText: TextView
    private lateinit var countdownText: TextView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set theme to follow system's dark/light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Set the layout for the sign-up screen
        setContentView(R.layout.activity_signup)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements
        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val signinlink = findViewById<TextView>(R.id.gologin)
        val togglePasswordVisibility = findViewById<ImageView>(R.id.togglePasswordVisibility)
        val usernameEditText = findViewById<EditText>(R.id.username)
        val emailEditText = findViewById<EditText>(R.id.newemailtxt)
        val passwordEditText = findViewById<EditText>(R.id.newpasstxt)
        val registerButton = findViewById<Button>(R.id.signupbutt)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        resendLinkText = findViewById(R.id.resendLinkText)
        countdownText = findViewById(R.id.countdownText)

        // Initially hide the resend text and countdown
        resendLinkText.visibility = TextView.GONE
        countdownText.visibility = TextView.GONE

        // Navigate to LoginActivity on clicking "Sign In"
        signinlink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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

        // Set onClickListener for Register Button
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show progress bar while registering
            progressBar.visibility = ProgressBar.VISIBLE

            // Register the user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Hide progress bar after the registration attempt
                    progressBar.visibility = ProgressBar.GONE
                    if (task.isSuccessful) {
                        // Save user data to Firebase Realtime Database
                        saveUserToDatabase(username, email)
                        // Send email verification
                        sendEmailVerification()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Set click listener for resend link text
        resendLinkText.setOnClickListener {
            sendEmailVerification()
        }
    }

    // Save user data to Firebase Realtime Database
    private fun saveUserToDatabase(username: String, email: String) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")

        val userMap = mapOf(
            "username" to username,
            "email" to email
        )

        reference.child(userId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Method to send email verification
    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent! Please check your inbox.", Toast.LENGTH_LONG).show()
                    showResendLinkText()
                    checkEmailVerification()
                } else {
                    Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Show the resend link text and start the countdown
    private fun showResendLinkText() {
        resendLinkText.visibility = TextView.GONE
        countdownText.visibility = TextView.VISIBLE
        startCountdown()
    }

    // Start the countdown timer for 15 seconds
    private fun startCountdown() {
        object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = "Resend in: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                countdownText.text = ""
                resendLinkText.visibility = TextView.VISIBLE
            }
        }.start()
    }

    // Check if the email is verified
    private fun checkEmailVerification() {
        val user = auth.currentUser
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                user?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful && user.isEmailVerified) {
                        saveLoginState()
                        navigateToMainActivity()
                    } else {
                        handler.postDelayed(this, 3000)
                    }
                }
            }
        }
        handler.post(runnable)
    }

    // Navigate to MainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Save the login state of the user
    private fun saveLoginState() {
        val sharedPrefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }
}
