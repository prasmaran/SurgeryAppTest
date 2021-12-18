package com.example.surgeryapptest.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.example.surgeryapptest.databinding.ActivityGeneralInfoWebBinding

class GeneralInfoWebActivity : AppCompatActivity() {


    private val args by navArgs<GeneralInfoWebActivityArgs>()

    private lateinit var binding: ActivityGeneralInfoWebBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralInfoWebBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val articleID = args.selectedArticle.articleId
        val articleTitle = args.selectedArticle.articleTitle
        val articleURL = args.selectedArticle.articleLink

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Article $articleID"
        supportActionBar?.subtitle = articleTitle

        binding.generalInfoWebView.webViewClient = WebViewClient()
        binding.generalInfoWebView.apply {
            loadUrl(articleURL)
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.webLoadingTv.visibility = View.GONE
        }, 2500)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.generalInfoWebView.canGoBack()) {
            binding.generalInfoWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
        //return super.onSupportNavigateUp()
    }
}