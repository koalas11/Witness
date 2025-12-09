package org.wdsl.witness.wearable

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmergencyRecording
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import org.wdsl.witness.wearable.theme.WitnessTheme
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val context = LocalContext.current
    WitnessTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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
    var isPressed by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF008B8B) else Color(0xFF3DCFDC),
        label = "Button Color Animation"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(2000L)
            Log.d("WearMessageService", "Long press after 2s")
            sendMessageToPhone("/WitnessWhistleMessage", context)
            isPressed = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(0.7f)
            .background(color = animatedColor, shape = CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease() // Wait until the finger is lifted
                        isPressed = false
                    },
                    onDoubleTap = {
                        Log.d("WearMessageService", "Double Tap Detected")
                        sendMessageToPhone("/WitnessHelpMessage", context)
                    },
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Action Button",
        )
    }
}

// Generic fun to send a message to the phone
private fun sendMessageToPhone(path: String, context: Context) {
    val messageClient = Wearable.getMessageClient(context)

    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            nodes.forEach { node ->
                Log.d(
                    "WearMessageService",
                    "Node: ${node.displayName} with ID: ${node.id}")

                messageClient.sendMessage(node.id, path, "start".toByteArray())
                    .addOnSuccessListener {
                        Log.d("WearMessageService", "Help message sent successfully")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("WearMessageService", "Error sending message", exception)
                    }
                }
            }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}