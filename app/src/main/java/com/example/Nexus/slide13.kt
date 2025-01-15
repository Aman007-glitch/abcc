package com.example.nexus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class slide13 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.slide3, container, false)

        view.findViewById<Button>(R.id.next2)?.setOnClickListener {
            // Navigate to LoginActivity after onboarding is complete
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // End onboarding and close the activity
        }

        return view
    }
}
