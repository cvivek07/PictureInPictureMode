package com.vivekchoudhary.pictureinpicturemode

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vivekchoudhary.pictureinpicturemode.ui.theme.PictureInPictureModeTheme

class MainActivity : ComponentActivity() {

    private val url = "https://www.ixigo.com/product-videos/inapp_compressed.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PictureInPictureModeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(onClick = {
                        launchPipActivity(url)
                    }, content = {
                        Text("Launch PIP Mode")
                    }, modifier = Modifier.wrapContentSize())
                }
            }
        }
    }

    private fun launchPipActivity(url: String) {
        startActivity(Intent(this, PipActivity::class.java).apply {
            putExtra(KEY_URL, url)
        })
    }

    companion object {
        val KEY_URL = "KEY_URL"
    }
}