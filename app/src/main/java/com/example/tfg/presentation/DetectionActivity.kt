package com.example.tfg.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
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
import android.os.CountDownTimer
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import com.example.tfg.R
import java.util.concurrent.TimeUnit

class DetectionActivity : Activity(), SensorEventListener, TextureView.SurfaceTextureListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var heartRateTextView: TextView
    private lateinit var mensajeTextView: TextView
    private lateinit var textureView: TextureView
    private var mediaPlayer: MediaPlayer? = null

    private val PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS
    private val REQUEST_CODE_SENSORS = 1

    private var lastBpm: Int = 0
    private var currentTextColor: Int = 0

    private var minBPM: Int = 45
    private var maxBPM: Int = 120
    private var timerHours: Int = 1

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        heartRateTextView = findViewById(R.id.heartRateTextView)
        mensajeTextView = findViewById(R.id.trackingTime)
        textureView = findViewById(R.id.textureView)
        val returnButton = findViewById<Button>(R.id.returnBtn)

        textureView.surfaceTextureListener = this
        checkPermissions()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        val prefs = getSharedPreferences("BPMSettings", Context.MODE_PRIVATE)
        minBPM = prefs.getInt("minBPM", 45)
        maxBPM = prefs.getInt("maxBPM", 120)
        timerHours = prefs.getInt("timerHours", 1)

        val durationMillis = timerHours * 3600 * 1000L

        startCountdown(durationMillis)

        returnButton.setOnClickListener {
            val intent = Intent(this, ChoiceActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    private fun startCountdown(durationMillis: Long) {
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                mensajeTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                mensajeTextView.text = "00:00:00"
                finish()
                // Aquí más adelante podrías abrir otra Activity con resultados del seguimiento
            }
        }.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values[0].toInt()
            if (bpm > 0) {
                lastBpm = bpm
                heartRateTextView.text = "$bpm bpm"

                // Determinar color según valores configurables
                val targetColor = when {
                    bpm in 60..100 -> 0xFF388E3C.toInt() // Verde
                    bpm in minBPM until 60 || bpm in 101..maxBPM -> 0xFFF57C00.toInt() // Naranja
                    bpm < minBPM || bpm > maxBPM -> 0xFFFF0000.toInt() // Rojo
                    else -> 0xFF808080.toInt() // Gris
                }

                // Transición suave de colores
                currentTextColor = ColorUtils.blendARGB(currentTextColor, targetColor, 0.1f)
                heartRateTextView.setTextColor(currentTextColor)
            }
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
        countDownTimer?.cancel()
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
            setDataSource(this@DetectionActivity, uri)
            setSurface(surface)
            isLooping = true
            setOnPreparedListener { it.start() }
            prepareAsync()
        }
    }
}
