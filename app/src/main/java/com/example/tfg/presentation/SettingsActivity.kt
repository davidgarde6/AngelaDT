package com.example.tfg.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tfg.R

class SettingsActivity : Activity() {

    private lateinit var maxBPMEditText: EditText
    private lateinit var minBPMEditText: EditText
    private lateinit var maxTimerDetectEditText: EditText

    private val PREFS_NAME = "BPMSettings"
    private val KEY_MAX_BPM = "maxBPM"
    private val KEY_MIN_BPM = "minBPM"
    private val KEY_DETECTION_TIMER_HOURS = "timerHours"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        maxBPMEditText = findViewById(R.id.maxBPMNumber)
        minBPMEditText = findViewById(R.id.minBPMNumber)
        maxTimerDetectEditText = findViewById(R.id.detectionTimer)
        val resetBPMButton = findViewById<Button>(R.id.resetBPM)
        val resetDetectionButton = findViewById<Button>(R.id.resetDetection)
        val backButton = findViewById<Button>(R.id.botonReturn)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var maxBPM = prefs.getInt(KEY_MAX_BPM, 120)
        var minBPM = prefs.getInt(KEY_MIN_BPM, 45)
        var timerHours = prefs.getInt(KEY_DETECTION_TIMER_HOURS, 1)

        maxBPMEditText.setText(maxBPM.toString())
        minBPMEditText.setText(minBPM.toString())
        maxTimerDetectEditText.setText(timerHours.toString())

        resetBPMButton.setOnClickListener {
            val defaultMax = 120
            val defaultMin = 45

            maxBPMEditText.setText(defaultMax.toString())
            minBPMEditText.setText(defaultMin.toString())

            prefs.edit()
                .putInt(KEY_MAX_BPM, defaultMax)
                .putInt(KEY_MIN_BPM, defaultMin)
                .apply()

            Toast.makeText(this, "Valores BPM reiniciados", Toast.LENGTH_SHORT).show()
        }

        resetDetectionButton.setOnClickListener {
            val defaultTimer = 1

            maxTimerDetectEditText.setText(defaultTimer.toString())

            prefs.edit()
                .putInt(KEY_DETECTION_TIMER_HOURS, defaultTimer)
                .apply()

            Toast.makeText(this, "Tiempo reiniciado a $defaultTimer h", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            val newMax = maxBPMEditText.text.toString().toIntOrNull()
            val newMin = minBPMEditText.text.toString().toIntOrNull()
            val newTimer = maxTimerDetectEditText.text.toString().toIntOrNull()

            if (newMax == null || newMin == null || newTimer == null) {
                Toast.makeText(this, "Introduce valores válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newMin >= 60) {
                Toast.makeText(this, "El mínimo debe ser menor que 60", Toast.LENGTH_SHORT).show()
                minBPMEditText.setText(minBPM.toString())
                return@setOnClickListener
            }

            if (newMax <= 100) {
                Toast.makeText(this, "El máximo debe ser mayor que 100", Toast.LENGTH_SHORT).show()
                maxBPMEditText.setText(maxBPM.toString())
                return@setOnClickListener
            }

            if (newMax < newMin) {
                Toast.makeText(this, "El máximo no puede ser menor que el mínimo", Toast.LENGTH_SHORT).show()
                maxBPMEditText.setText(maxBPM.toString())
                minBPMEditText.setText(minBPM.toString())
                return@setOnClickListener
            }

            maxBPM = newMax
            minBPM = newMin
            timerHours = newTimer

            prefs.edit()
                .putInt(KEY_MAX_BPM, maxBPM)
                .putInt(KEY_MIN_BPM, minBPM)
                .putInt(KEY_DETECTION_TIMER_HOURS, timerHours)
                .apply()

            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}
