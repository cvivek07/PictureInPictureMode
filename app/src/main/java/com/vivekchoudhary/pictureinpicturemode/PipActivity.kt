package com.vivekchoudhary.pictureinpicturemode

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vivekchoudhary.pictureinpicturemode.MainActivity.Companion.KEY_URL

class PipActivity : ComponentActivity() {

    class PipReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("Broadcast Receiver from PipActivity")
        }
    }

    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else {
            false
        }
    }

    private var videoViewBounds = Rect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidView(
                factory = { it ->
                    VideoView(it, null).apply {
                        setVideoURI(Uri.parse(intent.extras?.getString(KEY_URL)))
                        start()
                        setOnPreparedListener { it.isLooping = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .onGloballyPositioned {
                        videoViewBounds = it
                            .boundsInWindow()
                            .toAndroidRect()
                    }
            )
        }
        enterPipMode()
    }

    private fun updatePipParams(): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder()
                .setSourceRectHint(videoViewBounds)
                .setAspectRatio(Rational(3, 4))
                .setActions(
                    listOf(
                        RemoteAction(
                            Icon.createWithResource(
                                applicationContext,
                                R.drawable.baseline_info_24
                            ),
                            "alert icon",
                            "alert icon",
                            PendingIntent.getBroadcast(
                                applicationContext,
                                0,
                                Intent(applicationContext, PipReceiver::class.java),
                                PendingIntent.FLAG_IMMUTABLE
                            )
                        )
                    )
                )
                .build()
        } else {
            null
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipMode()
    }

    private fun enterPipMode(){
        if (!isPipSupported) {
            return
        }
        updatePipParams()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(it)
            }
        }
    }
}