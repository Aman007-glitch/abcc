package com.example.nexus

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClientActivity : ComponentActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var statusText: TextView
    private lateinit var startScanButton: Button
    private lateinit var wifiListView: ListView

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val results: List<ScanResult> = wifiManager.scanResults
            val wifiList = ArrayList<String>()

            for (scanResult in results) {
                wifiList.add(scanResult.SSID)  // Add SSID (network name) to the list
            }

            // Populate the ListView with available Wi-Fi networks
            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, wifiList)
            wifiListView.adapter = adapter
            statusText.text = "Available networks: ${wifiList.size}"

            // Disable the scan button if networks are found
            if (wifiList.isNotEmpty()) {
                startScanButton.isEnabled = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        // Initialize WifiManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Initialize UI elements
        statusText = findViewById(R.id.statusText)
        startScanButton = findViewById(R.id.startScanButton)
        wifiListView = findViewById(R.id.listview)

        // Request necessary permissions
        checkAndRequestPermissions()

        // Start scanning for available Wi-Fi networks
        startScanButton.setOnClickListener {
            startWifiScan()
        }

        // Handle Wi-Fi network click
        wifiListView.setOnItemClickListener { _, _, position, _ ->
            val selectedNetwork = wifiListView.getItemAtPosition(position).toString()
            connectToHotspot(selectedNetwork, "Fdb78sspfceef5l") // Replace with actual password
        }

        // Apply window insets for system bar handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clientLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Method to check and request permissions
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001
            )
        }
    }

    // Start Wi-Fi scan to discover networks
    private fun startWifiScan() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            wifiManager.startScan()  // Start scanning for Wi-Fi networks
        } else {
            Toast.makeText(this, "Location permission is required to scan for Wi-Fi", Toast.LENGTH_SHORT).show()
        }
    }

    // Connect to a selected Wi-Fi network (Hotspot)
    private fun connectToHotspot(ssid: String, password: String) {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"$ssid\""
        wifiConfig.preSharedKey = "\"$password\""

        // Remove previous configurations with the same SSID (if any)
        wifiManager.configuredNetworks?.forEach {
            if (it.SSID == "\"$ssid\"") {
                wifiManager.removeNetwork(it.networkId)
            }
        }

        // Add and enable the new Wi-Fi network configuration
        val netId = wifiManager.addNetwork(wifiConfig)
        if (netId != -1) {
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
            statusText.text = "Connecting to $ssid..."
            Toast.makeText(this, "Connecting to $ssid...", Toast.LENGTH_SHORT).show()
        } else {
            statusText.text = "Failed to connect to $ssid."
            Toast.makeText(this, "Failed to connect to hotspot.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the receiver to listen for the Wi-Fi scan results
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the receiver when the activity is paused
        unregisterReceiver(wifiScanReceiver)
    }
}
