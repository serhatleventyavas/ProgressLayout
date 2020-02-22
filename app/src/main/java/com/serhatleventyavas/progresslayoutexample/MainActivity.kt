package com.serhatleventyavas.progresslayoutexample

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.serhatleventyavas.progresslayout.ProgressLayoutListener

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        progressLayout.progressLayoutListener = object : ProgressLayoutListener {
            override fun onProgressChanged(seconds: Int) {
                textViewTime.text = "$seconds Seconds"
                if (seconds == 10) {
                    progressLayout.progressColor = Color.CYAN
                }
            }

            override fun onProgressCompleted() {
                Toast.makeText(this@MainActivity, "Completed", Toast.LENGTH_SHORT).show()
            }
        }
        progressLayout.start()
    }
}
