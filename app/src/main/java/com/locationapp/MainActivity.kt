package com.locationapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.text.style.ForegroundColorSpan
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit




class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        const val PERMISSIONS_REQUEST_COARSE_LOCATION = 1000
    }

    var periodicInterval = 60L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            periodicInterval = 15 // The MIN Accepted Interval
        }


        val textView = findViewById<TextView>(R.id.id_text_view)

        val spannable = SpannableString(textView.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Toast.makeText(applicationContext, "It works", Toast.LENGTH_LONG).show()
                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                            PERMISSIONS_REQUEST_COARSE_LOCATION)
                } else {
                    startWorker()
                }
            }

            override fun updateDrawState(ds: TextPaint?) {
                ds!!.isUnderlineText = false
            }
        }

        spannable.setSpan(clickableSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpans = spannable.getSpans(0, spannable.length, ForegroundColorSpan::class.java)
        for (colorSpan in colorSpans) {
            spannable.removeSpan(colorSpan)
        }

        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

    //Starts WorkManager
    fun startWorker() {
        val locationWork = PeriodicWorkRequest.Builder(LocationWorker::class.java, periodicInterval, TimeUnit.MINUTES).addTag(LocationWorker.TAG).build()
        WorkManager.getInstance().enqueue(locationWork)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_COARSE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startWorker()
                } else {
                    Toast.makeText(this, this.getString(R.string.location_required), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
