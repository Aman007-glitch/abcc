package com.example.nexus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class onboarding : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboard_slidng) // Ensure this layout has a fragment container

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
}
