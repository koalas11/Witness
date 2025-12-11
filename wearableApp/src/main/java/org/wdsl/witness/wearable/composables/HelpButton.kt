package org.wdsl.witness.wearable.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmergencyRecording
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Handles the only button in the wearable app
 **/
@Composable
fun HelpButton(
    isConfirmed: Boolean,
    whistleLongPress: Boolean,
    isPressed: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {

    // Animate the color of the button (when pressed it changes color)
    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF008B8B) else Color(0xFF3DCFDC),
        label = "Button Color Animation"
    )

    // Icon to show in the button (usually the camera, when holding the whistle)
    val iconToShow = if(!whistleLongPress) Icons.Filled.EmergencyRecording else Icons.Filled.Sports

    // Change the icon color to red when the recording has started on the phone
    val iconColor = if(isConfirmed && !whistleLongPress) Color.Red else Color.Black

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (whistleLongPress) {
            /***
             * Circular porgress bar when the button is hold to activate the whistle
             * Sources:
             * https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-circular-progress-indicator.html
             * https://developer.android.com/reference/com/google/android/material/progressindicator/CircularProgressIndicator
             */
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.8f),
                progress = { progress },
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 12.dp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .background(color = animatedColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(0.8f),
                imageVector = iconToShow,
                contentDescription = "Emergency Button",
                tint = iconColor
            )
        }
    }
}