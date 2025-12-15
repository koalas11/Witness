package org.wdsl.witness.wearable

import android.content.Context
import android.os.Bundle
import android.os.SystemClock.uptimeMillis
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastCoerceIn
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wdsl.witness.wearable.theme.WitnessTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    WitnessTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            SendHelp()
        }
    }
}

@Composable
fun SendHelp(holdMs: Long = 1.5.seconds.inWholeMilliseconds) {
    var progress by remember { mutableFloatStateOf(0f) }
    var confirmed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize(0.7f)
            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        confirmed = false
                        scope.launch {
                            val job = launch {
                                val start = uptimeMillis()
                                while (isActive) {
                                    val elapsed = uptimeMillis() - start + 50.milliseconds.inWholeMilliseconds
                                    progress = (elapsed.toFloat() / holdMs.toFloat()).fastCoerceIn(0f, 1.05f)
                                    if (progress >= 1.05f && !confirmed) {
                                        delay(50.milliseconds)
                                        confirmed = true
                                        sendMessageToPhone("/WitnessHelpMessage", context)
                                        break
                                    }
                                    delay(50.milliseconds)
                                }
                            }
                            tryAwaitRelease()
                            job.cancel()
                            progress = 0f
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = null,
        )

        if (progress > 0f && !confirmed) {
            CircularProgressIndicator(
                progress = { progress.fastCoerceAtMost(1f) },
                modifier = Modifier
                    .matchParentSize()
                    .padding(8.dp),
                strokeWidth = 4.dp
            )
        }
    }
}

// Generic fun to send a message to the phone
private fun sendMessageToPhone(path: String, context: Context) {
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