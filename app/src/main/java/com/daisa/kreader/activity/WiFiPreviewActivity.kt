package com.daisa.kreader.activity

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.daisa.kreader.Constants
import com.daisa.kreader.R
import com.daisa.kreader.databinding.ActivityWiFiPreviewBinding

//todo add support for APIs below Q
//fixme wifi is stored on the app, so when it's uninstalled the wifi gets removed (check WifiNetworkSpecifier)
class WiFiPreviewActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityWiFiPreviewBinding

    private lateinit var tvSsid: TextView
    private lateinit var tvWiFiPwd: TextView
    private lateinit var tvWiFiType: TextView

    lateinit var wifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWiFiPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val ssid = intent.getStringExtra("ssid")!!
        val pwd = intent.getStringExtra("pwd")!!
        val type = intent.getIntExtra("type", 0)
        tvSsid = findViewById(R.id.btConnectWiFi)

        tvSsid = viewBinding.tvSsid.apply {
            text = getString(R.string.ssid, ssid)
        }
        tvWiFiPwd = viewBinding.tvWiFiPwd.apply {
            text = getString(R.string.password, pwd)
        }
        tvWiFiType = viewBinding.tvWiFiType.apply {
            text = getString(R.string.type, type)
        }

        viewBinding.btConnectWiFi.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val suggestion = WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(pwd)
                    //.setIsAppInteractionRequired(true) // Optional (Needs location permission)
                    .build()

                val suggestionsList = ArrayList<WifiNetworkSuggestion>().apply {
                    add(suggestion)
                }

                wifiManager = baseContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

                val status = wifiManager.addNetworkSuggestions(suggestionsList)
                if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                    // do error handling here…
                    Log.d(Constants.TAG, "No se ha podido conectar al wifi")
                } else if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                    Log.d(Constants.TAG, "Wifi conectado")
                }
            } else {
                TODO("VERSION.SDK_INT < Q")
            }

        }


    }
}