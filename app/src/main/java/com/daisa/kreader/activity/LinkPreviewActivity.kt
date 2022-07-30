package com.daisa.kreader.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.daisa.kreader.R
import com.daisa.kreader.databinding.ActivityLinkPreviewBinding

//todo set javaScriptEnabled from user preferences
//https://developer.android.com/design-for-safety#security
//https://developer.android.com/docs/quality-guidelines/core-app-quality#sc
@SuppressLint("SetJavaScriptEnabled")
class LinkPreviewActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLinkPreviewBinding

    private val webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLinkPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val webview = viewBinding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            settings.allowFileAccess = false
        }

        val url = intent.getStringExtra("link")!!

        viewBinding.tvURl.text = getString(R.string.url_found, url)

        webview.loadUrl(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView!!.canGoBack()) {
            webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}