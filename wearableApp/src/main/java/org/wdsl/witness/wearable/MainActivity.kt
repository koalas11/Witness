package org.wdsl.witness.wearable

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.wearable.Wearable
import org.wdsl.witness.wearable.theme.WitnessTheme

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
fun SendHelp() {
    var lastTapTime by remember { mutableLongStateOf(0L) }
//    Button(
//        onClick = {
//            // Double tap detection
//            val currentTime = System.currentTimeMillis()
//            if(currentTime - lastTapTime < 400L) {
//                sendMessageToPhone("/WitnessHelpMessage", context)
//            }
//            lastTapTime = currentTime
//        },
//        Modifier
//            .size(140.dp)
//            .background(MaterialTheme.colors.primary)
//            .clip(CircleShape),
//        shape = CircleShape
//    )
//    {
//        Text(
//            stringResource(R.string.send_help),
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//            fontSize = 24.sp
//        )
//    }
    val context = LocalContext.current
    IconButton (
        modifier = Modifier
            .fillMaxSize(0.7f)
            .background(color = Color(0xFF3DCFDC), shape = CircleShape),
        onClick = {
            // Double tap detection
            val currentTime = System.currentTimeMillis()
            if(currentTime - lastTapTime < 400L) {
                sendMessageToPhone("/WitnessHelpMessage", context)
            }
            lastTapTime = currentTime
        },
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(0.8f),
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = "Stop Emergency Recording",
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