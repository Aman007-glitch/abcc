package com.example.nexus
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge mode
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Set the layout for the activity
        setContentView(R.layout.activity_main)
       val recyclerView: RecyclerView = findViewById(R.id.recyclerViewHistory)

         //Sample data for connection history
        val connectionHistoryList = listOf(
            ConnectionHistory("John's Hotspot", "25 Jan 2025", "1.2 GB"),
            ConnectionHistory("Cafe Wi-Fi", "24 Jan 2025", "500 MB"),
            ConnectionHistory("Office Wi-Fi", "23 Jan 2025", "3.4 GB")
        )
        Log.d("MainActivity", "List size: ${connectionHistoryList.size}")

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        runOnUiThread {
            recyclerView.adapter = ConnectionHistoryAdapter(connectionHistoryList)
        }
        // Get the username passed via intent and set the welcome message
       // val username = intent.getStringExtra("username")
        // Set welcome message if needed
        // val usernameTextView = findViewById<TextView>(R.id.instructionText)
        // usernameTextView.text = "Welcome, $username"

        // Adjust layout for system insets (status bar, navigation bar, etc.)
        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Set padding to avoid content being covered by system bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Return insets after applying the padding
        }

        // Set up BottomNavigationView listener
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profileuser-> {
                    // Open ProfileActivity when the profile icon is clicked
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                // Handle other items if necessary
                else -> false
            }
        }

        // Check initial network status
        checkNetworkAndUpdateTitle()

        // Register a network callback to listen for network status changes
        registerNetworkCallback()
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
        finish() // Close MainActivity so the user can't navigate back to it
    }

    // Function to check network status and update the toolbar title
    private fun checkNetworkAndUpdateTitle() {
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout)
        val status = if (isConnected) "  You are Online" else "  You are Offline"
        collapsingToolbarLayout.title = status  // Update the title dynamically
    }

    // Function to listen for network changes and update the status
    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
                checkNetworkAndUpdateTitle() // Update UI when connected
            }

            override fun onLost(network: Network) {
                isConnected = false
                checkNetworkAndUpdateTitle() // Update UI when disconnected
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }
}
