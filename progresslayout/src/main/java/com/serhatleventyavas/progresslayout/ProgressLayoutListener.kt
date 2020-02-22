package com.serhatleventyavas.progresslayout

interface ProgressLayoutListener {
    fun onProgressCompleted()
    fun onProgressChanged(seconds: Int)
}