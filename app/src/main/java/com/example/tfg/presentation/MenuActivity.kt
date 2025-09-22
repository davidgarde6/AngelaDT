package com.example.tfg.presentation

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.tfg.R

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val botonOpciones = findViewById<Button>(R.id.botonOpciones)
        val botonAjustes = findViewById<Button>(R.id.botonAjustes)

        botonOpciones.setOnClickListener {
            val intent = Intent(this, ChoiceActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish();
        }

        botonAjustes.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish();
        }
    }
}