package com.brewfinder.app.ui.base_screen.activity.agreement

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.brewfinder.app.R
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import kotlinx.android.synthetic.main.user_agreement.*

/**
 *@author Rashmi
 * 10/10/2019
 */
class UserAgreementActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_agreement)
        webView.webViewClient = MyWebViewClient(this)

        webView.loadUrl("https://www.javatpoint.com/")

    }

    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }
}
