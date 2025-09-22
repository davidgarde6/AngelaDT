package com.example.tfg.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.tfg.R

class ChoiceActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_choice)

        val botonDeteccion = findViewById<Button>(R.id.botonOpciones)
        val botonTracking = findViewById<Button>(R.id.botonTracking)
        val botonVolver = findViewById<Button>(R.id.botonReturn)

        botonDeteccion.setOnClickListener {
            val intent = Intent(this, DetectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        botonTracking.setOnClickListener {
            // Aquí irá el modo envío más adelante
        }

        botonVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }
}
