package com.example.nexus

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class slide11 : Fragment() {

    private lateinit var onboardingActivity: onboarding


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is onboarding) {
            onboardingActivity = context
        } else {
            throw IllegalStateException("Parent activity must be of type OnboardingActivity")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.slide1, container, false)

        view.findViewById<Button>(R.id.next).setOnClickListener {
            onboardingActivity.replaceFragment(slide12(), 1)
        }

        return view
    }
}
