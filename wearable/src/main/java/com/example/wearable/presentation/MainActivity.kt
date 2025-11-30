package com.example.wearable.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.wearable.R
import com.example.wearable.presentation.theme.WitnessTheme
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(context: String) {
    val context = LocalContext.current
    WitnessTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            SendHelp(context)
        }
    }
}

@Composable
fun SendHelp(context: Context) {
    var lastTapTime by remember { mutableLongStateOf(0L) }
    Button(
        onClick = {
            val currentTime = System.currentTimeMillis()

            if(currentTime - lastTapTime < 400L) {
                sendHelpMessage("/startRecording", context)
            }

            lastTapTime = currentTime
        },
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
    {
        Text(stringResource(R.string.send_help))
    }
}

private fun sendHelpMessage(path: String, context: Context) {
    val messageClient = Wearable.getMessageClient(context)

    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            nodes.forEach { node ->
                Log.d("WearSend", "Nodo: ${node.displayName} and ${node.id}")

                messageClient.sendMessage(node.id, path, "start".toByteArray())
                    .addOnSuccessListener {
                        Log.d("WearSend", "Message sent successfully")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("WearSend", "Error sending message", exception)
                    }
                }
            }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}