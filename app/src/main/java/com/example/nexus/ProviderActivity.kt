package com.example.nexus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import com.google.firebase.auth.FirebaseAuth
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pConfig
//import android.net.wifi.p2p.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.WifiP2pDevice
//import android.net.wifi.p2p.WpsInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo

class ProviderActivity : ComponentActivity() {

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var statusText: TextView
    private lateinit var startHotspotButton: Button
    private lateinit var stopHotspotButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the UI
        setContentView(R.layout.activity_provider)

        // Initialize the WifiP2pManager and channel
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)

        // Initialize UI elements
        statusText = findViewById(R.id.statusText)
        startHotspotButton = findViewById(R.id.startHotspotButton)
        stopHotspotButton = findViewById(R.id.stopHotspotButton)

        // Start the hotspot and advertise service
        startHotspotButton.setOnClickListener {
            startMobileHotspot()
            advertiseService()
            statusText.text = "Hotspot is active. Waiting for connections..."
        }

        // Stop the hotspot
        stopHotspotButton.setOnClickListener {
            stopHotspot()
            statusText.text = "Hotspot stopped."
        }

        // Apply window insets (for handling system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.providerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Method to advertise the Wi-Fi Direct service
    private fun advertiseService() {
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "Nexus_Hotspot", "_presence._tcp", mapOf("ssid" to "MyHotspot")
        )

        wifiP2pManager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WiFiDirect", "Service added successfully")
            }

            override fun onFailure(reason: Int) {
                Log.e("WiFiDirect", "Failed to add service: $reason")
            }
        })
    }

    // Method to enable the Mobile Hotspot (via settings)
    private fun startMobileHotspot() {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivity(intent)  // Opens hotspot settings for user to enable
    }

    // Method to stop the hotspot (this is a placeholder, for root-enabled devices, you could stop it programmatically)
    private fun stopHotspot() {
        // Logic to stop the hotspot (e.g., disable mobile hotspot programmatically, if possible)
    }
}
