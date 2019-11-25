package com.hites.flashlight

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest.permission
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    var flashlightStatus = false

    private lateinit var rootView: View
    private lateinit var cameraManager: CameraManager
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isEnabled = ContextCompat.checkSelfPermission(
            this,
            permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if(!isEnabled){
            ActivityCompat.requestPermissions(this,
                arrayOf(permission.CAMERA), 50)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("FlashlightApp", "camera2 selected");
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        } else {
            Log.d("FlashlightApp", "camera1 selected");
            camera = Camera.open()
        }

        imageView = findViewById(R.id.flashlight)
        rootView = imageView.rootView
        rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

        imageView.setOnClickListener {
            if(checkCameraHardware(this)){
                if(flashlightStatus){
                    flashlightOff()
                }else{
                    flashlightOn()
                }
            } else {
                Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }


    private fun flashlightOn() {
        flashlightStatus = true
        rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, flashlightStatus)
        } else{
            val p = camera.parameters
            p.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            camera.parameters = p
            camera.startPreview()
        }
    }

    private fun flashlightOff() {
        flashlightStatus = false
        rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, flashlightStatus)
        } else {
            val p = camera.parameters
            p.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera.parameters = p
            camera.startPreview()
        }
    }
}
