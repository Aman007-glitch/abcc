package com.example.Nexus

    import android.content.Intent
    import android.content.SharedPreferences
    import android.os.Bundle
    import androidx.activity.R
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat

    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
           
            enableEdgeToEdge()
            setContentView(com.example.Nexus.R.layout.activity_main)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.example.Nexus.R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }}
        }
    


