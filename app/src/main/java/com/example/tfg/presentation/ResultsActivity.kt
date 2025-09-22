package com.example.tfg.presentation

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.tfg.R

class ResultsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val bpm = intent.getIntExtra("bpm", -1)

        val animationView = findViewById<LottieAnimationView>(R.id.heartbeatAnimation)
        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        val bpmText = findViewById<TextView>(R.id.bpmResult)
        val estadoText = findViewById<TextView>(R.id.textoMidiendo)

        animationView.setMinAndMaxFrame(0, 60)
        animationView.speed = 1.0f
        animationView.playAnimation()

        bpmText.text = "$bpm bpm"

        when {
            bpm in 60..100 -> {
                estadoText.text = "Ritmo cardiaco en orden"
                estadoText.setTextColor(Color.parseColor("#388E3C")) // Verde
                bpmText.setTextColor(Color.parseColor("#388E3C"))
                frameLayout.setBackgroundColor(Color.parseColor("#C8E6C9"))
            }
            (bpm in 45 until 60) || (bpm in 101..120) -> {
                estadoText.text = "Algo fuera de lo ideal"
                estadoText.setTextColor(Color.parseColor("#F57C00")) // Naranja
                bpmText.setTextColor(Color.parseColor("#F57C00"))
                frameLayout.setBackgroundColor(Color.parseColor("#FFE0B2"))
            }
            (bpm < 45) || (bpm > 120) -> {
                estadoText.text = "RIESGO"
                estadoText.setTextColor(Color.RED)
                bpmText.setTextColor(Color.RED)
                frameLayout.setBackgroundColor(Color.parseColor("#FFCDD2"))
            }
            else -> {
                estadoText.text = "Medición fallida"
                estadoText.setTextColor(Color.GRAY)
                frameLayout.setBackgroundColor(Color.LTGRAY)
            }
        }
    }
}
