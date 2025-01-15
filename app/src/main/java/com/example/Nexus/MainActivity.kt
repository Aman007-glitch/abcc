package com.example.nexus

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge mode
        enableEdgeToEdge()

        // Set the layout for the activity
        setContentView(R.layout.activity_main)  // Reference R.layout.activity_main directly

        // Adjust layout for system insets (status bar, navigation bar, etc.)
        val mainView = findViewById<View>(R.id.main)  // Assuming you have a view with this ID in your layout
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Set padding to avoid content being covered by system bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Return insets after applying the padding
        }
    }
}
