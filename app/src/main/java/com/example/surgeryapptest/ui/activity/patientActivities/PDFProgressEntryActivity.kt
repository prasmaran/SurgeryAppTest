package com.example.surgeryapptest.ui.activity.patientActivities

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.example.surgeryapptest.databinding.ActivityPdfprogressEntryBinding
import com.example.surgeryapptest.utils.network.responses.NetworkResult
import com.example.surgeryapptest.view_models.patient.PDFGenerateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PDFProgressEntryActivity : AppCompatActivity() {

    private val args by navArgs<PDFProgressEntryActivityArgs>()
    private lateinit var binding: ActivityPdfprogressEntryBinding
    private val pdfGenerateViewModel: PDFGenerateViewModel by viewModels()

    /**
     * testing
     */
    var reportLoadError = false
    var queId: Long? = null
    lateinit var receiver: BroadcastReceiver
    private lateinit var downloadManager: DownloadManager
    private lateinit var pdfUrl: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfprogressEntryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val entryID = args.woundImageId.toString()

        generateWoundPDF(entryID)

    }

    /*
    * Broadcast receiver which handles result of file download
    *
    * */

    @SuppressLint("NewApi")
    private fun generateWoundPDF(entryID: String) {

        binding.pdfGenerateProgressBar.visibility = View.VISIBLE

        pdfGenerateViewModel.generateWoundPDF(entryID)
        pdfGenerateViewModel.pdfGenerateResponse.observe(this@PDFProgressEntryActivity, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.pdfGenerateProgressBar.visibility = View.GONE
                    val pdfRetrieveResponse = response.data?.message.toString()
                    Toast.makeText(
                        this,
                        pdfRetrieveResponse,
                        Toast.LENGTH_SHORT
                    ).show()
                    pdfUrl = response.data?.result.toString()

                    /**
                     * initiate the show pdf methods here
                     */

                    initWebView()
                    initShare()
                    initBroadcastReceiver()
                    registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                }
                is NetworkResult.Error -> {
                    binding.pdfGenerateProgressBar.visibility = View.GONE
                    Toast.makeText(
                        this@PDFProgressEntryActivity,
                        response.data?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    // TODO
                }
            }
        })
    }

    private fun initBroadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            @SuppressLint("Range")
            override fun onReceive(p0: Context?, p1: Intent?) {
                toast("On Receive")
                val action = p1!!.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    var requestQuery = DownloadManager.Query()
                    requestQuery.setFilterById(queId!!)
                    val cursor = downloadManager.query(requestQuery)
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            toast("Download Completed")
                            val uriString =
                                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                            val uri = Uri.parse(uriString)
                            val share = Intent()
                            share.action = Intent.ACTION_SEND
                            share.type = "application/pdf"
                            share.putExtra(Intent.EXTRA_STREAM, uri)
                            startActivity(share)
                        } else if (DownloadManager.STATUS_FAILED == cursor.getInt(columnIndex)) {
                            toast("Download Failed")
                        }
                    }

                }
            }

        }
    }

    /**
     * Function which starts downloading the file from given url
     * */
    private fun initShare() {
        binding.pdfShareDownloadTv.setOnClickListener {
            toast("Downloading...")
            downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri =
                Uri.parse(pdfUrl)
            val request: DownloadManager.Request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            queId = downloadManager.enqueue(request)

        }
    }

    /*
    * Function which loads content in to web view
    * */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        reportLoadError = false

        binding.pdfWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(webview: WebView, url: String, favicon: Bitmap?) {
                webview.visibility = View.INVISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {

                //handles load error
                if (reportLoadError) {
                    toast("Error in downloading...")
                    //handles successful pdf load
                } else {
                    view.visibility = View.VISIBLE
                }
                super.onPageFinished(view, url)

            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                reportLoadError = true

            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view!!.loadUrl(request!!.url.toString())
                return false
            }

        }
        binding.pdfWebView.settings.javaScriptEnabled = true
        binding.pdfWebView.settings.domStorageEnabled = true
        binding.pdfWebView.overScrollMode = WebView.OVER_SCROLL_NEVER

        binding.pdfWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdfUrl")

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
        //return super.onSupportNavigateUp()
    }

}

// Extension function to show a simple toast
fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}