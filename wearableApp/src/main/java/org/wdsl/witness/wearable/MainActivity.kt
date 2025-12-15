package org.wdsl.witness.wearable

import android.R.style.Theme_DeviceDefault
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import org.wdsl.witness.shared.WearableMessageConstants
import org.wdsl.witness.wearable.composables.HelpButton
import org.wdsl.witness.wearable.theme.WitnessTheme
import org.wdsl.witness.wearable.util.EmergencyRecordingMessageState

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(Theme_DeviceDefault)

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
            SendHelpScreen(context)
        }
    }
}

@Composable
fun SendHelpScreen(context: Context) {

    // Tracks whether the button is currently pressed
    var isPressed by remember { mutableStateOf(false) }

    // Tracks if the button is being held
    var whistleLongPress by remember { mutableStateOf(false) }

    // Observes the emergency recording state coming from the phone
    val isEmergencyRecording by EmergencyRecordingMessageState.isEmergencyRecording.collectAsState()

    // Value used to animate the circular progress indicator
    val progress = remember { Animatable(0f) }

    /**
     * Handles long-press logic:
     * - waits for a short delay to distinguish from a tap
     * - shows the whistle UI
     * - animates the progress bar
     * - triggers the whistle message if the press lasts long enough (2s)
     */
    LaunchedEffect(isPressed) {
        if (isPressed) {
            progress.snapTo(0f)
            delay(200L)
            whistleLongPress = true;
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
            )
            Log.d("WearMessageService", "Long press after 2s")
            sendMessageToPhone(WearableMessageConstants.WHISTLE_MESSAGE_PATH, context)
        }
    }

    HelpButton(
        isConfirmed = isEmergencyRecording,
        whistleLongPress = whistleLongPress,
        isPressed = isPressed,
        progress = progress.value,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                // Called when the user presses and holds the button
                onPress = {
                    isPressed = true
                    tryAwaitRelease() // Wait until the finger is lifted
                    whistleLongPress = false;
                    isPressed = false
                },
                onDoubleTap = {
                    Log.d("WearMessageService", "Double Tap Detected")
                    sendMessageToPhone(WearableMessageConstants.HELP_MESSAGE_PATH, context)
                },
            )
        },
    )
}


/**
 * Sends a message to all connected nodes (phones)
 * using the Wearable Message API
 *
 * @param path Message path identifying the action to perform
 * @param context Application context
 */
fun sendMessageToPhone(path: String, context: Context) {
    val messageClient = Wearable.getMessageClient(context)
    Log.d("WearMessageService", "Sending message to phone with path: $path")
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
