package com.example.pdfcreator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var convertTextButton: Button
    private lateinit var convertImageButton: Button
    private lateinit var viewPdfButton: Button
    private var permissionStatus: Boolean = false
    private val permissions = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            "android.permission.READ_MEDIA_AUDIO",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.ACCESS_MEDIA_LOCATION"
        )
    } else {
        arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_MEDIA_LOCATION"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        convertTextButton = findViewById(R.id.convertTextButton)
        convertImageButton = findViewById(R.id.convertImageButton)
        viewPdfButton = findViewById(R.id.viewPdfButton)

        convertTextButton.setOnClickListener {
            checkPermissionsAndProceed(ConvertTextToPdf::class.java)
        }

        convertImageButton.setOnClickListener {
            checkPermissionsAndProceed(ConvertImageToPdf::class.java)
        }

        viewPdfButton.setOnClickListener {
            checkPermissionsAndProceed(ViewPdf::class.java)
        }
    }

    private fun checkPermissionsAndProceed(destinationClass: Class<*>) {
        if (hasPermissions()) {
            val intent = Intent(this@MainActivity, destinationClass)
            startActivity(intent)
        } else {
            requestPermissions(permissions, 80)
        }
    }

    private fun hasPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
                else{
                    return true
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 80) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionStatus = true
                Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission Denied. Allow permissions to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
