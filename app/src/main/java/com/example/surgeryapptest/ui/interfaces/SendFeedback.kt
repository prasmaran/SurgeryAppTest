package com.example.surgeryapptest.ui.interfaces

interface SendFeedback {
    fun sendData(feedback: String, isEmpty: Boolean = false)
}