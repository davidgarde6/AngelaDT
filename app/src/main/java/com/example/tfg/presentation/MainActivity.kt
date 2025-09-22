package com.example.tfg.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tfg.R
import com.example.tfg.presentation.MenuActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { VideoFullScreen(this)
        }
    }
}
@Composable
fun VideoFullScreen(activity: ComponentActivity) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.intro_tfg}")
                setVideoURI(videoUri)
                setOnPreparedListener { it.isLooping = false }

                setOnCompletionListener {
                    val intent = Intent(activity, MenuActivity::class.java)
                    activity.startActivity(intent)

                    activity.overridePendingTransition(R.anim.fade_in, R.anim.stay)

                    activity.finish()
                }
                start()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
