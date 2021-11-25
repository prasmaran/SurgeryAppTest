package com.example.surgeryapptest.ui.streamChat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.surgeryapptest.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StreamChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_chat)
    }
}