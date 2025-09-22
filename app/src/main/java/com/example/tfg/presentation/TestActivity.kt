package com.example.tfg.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.tfg.R
import com.example.tfg.presentation.ResultsActivity

class TestActivity : Activity(), SensorEventListener, TextureView.SurfaceTextureListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null

    private lateinit var heartRateTextView: TextView
    private lateinit var textureView: TextureView
    private var mediaPlayer: MediaPlayer? = null

    private val PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS
    private val REQUEST_CODE_SENSORS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val textoMidiendo = findViewById<TextView>(R.id.textoMidiendo)

        val fadeAnimation = AlphaAnimation(1.0f, 0.2f).apply {
            duration = 1000 // milisegundos por ciclo
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        textoMidiendo.startAnimation(fadeAnimation)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        heartRateTextView = findViewById(R.id.heartRateTextView)
        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = this

        checkPermissions()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("bpm", lastBpm)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.stay)
            finish()
        }, 30_000)
    }
    private var lastBpm: Int = 0

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values[0].toInt()
            lastBpm = bpm
            heartRateTextView.text = "$bpm bpm"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SENSORS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Se necesita el permiso para leer pulsaciones", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(PERMISSION_BODY_SENSORS), REQUEST_CODE_SENSORS)
        }
    }

    // --- TextureView callbacks ---
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        playVideo(Surface(surface))
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mediaPlayer?.release()
        mediaPlayer = null
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    private fun playVideo(surface: Surface) {
        val uri = Uri.parse("android.resource://$packageName/${R.raw.test_tfg}")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@TestActivity, uri)
            setSurface(surface)
            isLooping = true
            setOnPreparedListener { it.start() }
            prepareAsync()
        }
    }
}
