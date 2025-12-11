package org.wdsl.witness.wearable

import android.R.style.Theme_DeviceDefault
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import org.wdsl.witness.shared.WearableMessageConstants
import org.wdsl.witness.wearable.composables.HelpButton
import org.wdsl.witness.wearable.theme.WitnessTheme
import org.wdsl.witness.wearable.util.ConfirmationMessageState

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
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var isPressed by remember { mutableStateOf(false) }
    var whistleLongPress by remember { mutableStateOf(false) }
    val isConfirmed by ConfirmationMessageState.isConfirmed.collectAsState()

    val progress = remember { Animatable(0f) }

    // When the button is being hold, wait 200ms and then change the icon and start the
    // animation of the progress bar
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200L)
            whistleLongPress = true;
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1800)
            )
            Log.d("WearMessageService", "Long press after 2s")
            sendMessageToPhone(WearableMessageConstants.WHISTLE_MESSAGE_PATH, context)
        }
    }

    HelpButton(
        isConfirmed = isConfirmed,
        whistleLongPress = whistleLongPress,
        isPressed = isPressed,
        progress = progress.value,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
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


// Generic fun to send a message to the phone with a specific path
fun sendMessageToPhone(path: String, context: Context) {
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