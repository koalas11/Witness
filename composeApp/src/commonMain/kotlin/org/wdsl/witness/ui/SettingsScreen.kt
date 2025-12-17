package org.wdsl.witness.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.WitnessBuildConfig
import org.wdsl.witness.model.EmergencyGesturesStatus
import org.wdsl.witness.state.AppSettingsState
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.util.AUDIO_RECORDING_PERMISSION
import org.wdsl.witness.util.COARSE_LOCATION_PERMISSION
import org.wdsl.witness.util.READ_CONTACTS_PERMISSION
import org.wdsl.witness.util.SMS_PERMISSION
import org.wdsl.witness.viewmodel.AppViewModel

/**
 * A composable function that represents the Settings screen of the application.
 *
 * @param modifier The modifier to be applied to the SettingsScreen.
 * @param appViewModel The ViewModel that holds the application state and handles navigation.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = modifier,
        ) {
            val platformContext = LocalPlatformContext.current
            val appSettingsChanged by AppSettingsState.settingsChanged.collectAsStateWithLifecycle()
            val permissions = listOf(
                COARSE_LOCATION_PERMISSION,
                AUDIO_RECORDING_PERMISSION,
                SMS_PERMISSION,
                READ_CONTACTS_PERMISSION,
            )
            var permissionsMissing by rememberSaveable(appSettingsChanged) {
                mutableStateOf(
                    permissions.map {
                        it.id to fastUIActions.checkPermissionsStatus(
                            platformContext,
                            it.id,
                        )
                    }
                )
            }
            val allGranted = permissionsMissing.all { it.second }
            if (allGranted) {
                Row(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = modifier
                            .padding(end = 8.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                    )
                    Text(
                        modifier = modifier,
                        text = "All required permissions granted.",
                    )
                }
            } else {
                Row(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = modifier
                            .padding(end = 8.dp),
                        imageVector = Icons.Default.NotificationImportant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        modifier = modifier,
                        text = "Some required permissions are missing:",
                    )
                }
                permissionsMissing.forEach { (permissionId, granted) ->
                    val permission = permissions.first { it.id == permissionId }
                    if (!granted) {
                        Row(
                            modifier = modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = modifier
                                    .padding(start = 32.dp),
                                imageVector = permission.icon,
                                contentDescription = null,
                            )
                            Text(
                                modifier = modifier
                                    .padding(start = 4.dp, end = 8.dp),
                                text = permission.name,
                            )
                            fastUIActions.PermissionRequestDialog(
                                modifier = modifier,
                                permission = permission,
                            )
                        }
                    }
                }
            }

            if (!allGranted) {
                Button(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    onClick = {
                        fastUIActions.openSystemAppSettings(platformContext)
                    },
                ) {
                    Text("Fast travel to App Settings")
                }
            }

            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            val emergencyGesturesStatus by AppSettingsState.accessibilityServiceEnabled.collectAsStateWithLifecycle()
            Row(
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = modifier
                        .padding(end = 8.dp),
                    imageVector = if (emergencyGesturesStatus == EmergencyGesturesStatus.ENABLED) {
                        Icons.Default.Check
                    } else {
                        Icons.Default.NotificationImportant
                    },
                    contentDescription = null,
                    tint = if (emergencyGesturesStatus == EmergencyGesturesStatus.ENABLED) {
                        LocalContentColor.current
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
                Text(
                    modifier = modifier.padding(8.dp),
                    text = buildAnnotatedString {
                        append("Accessibility Service Status: ")
                        if (emergencyGesturesStatus == EmergencyGesturesStatus.ENABLED) {
                            append(emergencyGesturesStatus.name)
                        } else {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                append(emergencyGesturesStatus.name)
                            }
                        }
                    },
                )
            }
            if (emergencyGesturesStatus != EmergencyGesturesStatus.ENABLED) {
                Button(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    onClick = {
                        fastUIActions.openAccessibilityServicesSettings(platformContext)
                    },
                ) {
                    Text("Fast travel to Accessibility Settings")
                }
                Text(
                    modifier = modifier
                        .padding(8.dp),
                    text = "Emergency Gestures require the Accessibility Service to be enabled.",
                )
            }
        }

        Row(
            modifier = modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.Start)
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        appViewModel.navigateTo(ScreenRoute.GeneralSettings)
                    }
                ),
        ) {
            Icon(
                modifier = modifier
                    .padding(8.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = null,
            )
            Text(
                modifier = modifier
                    .padding(8.dp),
                text = "General Settings",
            )
        }
        Row(
            modifier = modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.Start)
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        appViewModel.navigateTo(ScreenRoute.SmsSettings)
                    }
                ),
        ) {
            Icon(
                modifier = modifier
                    .padding(8.dp),
                imageVector = Icons.Default.Sms,
                contentDescription = null,
            )
            Text(
                modifier = modifier
                    .padding(8.dp),
                text = "Emergency SMS Settings",
            )
        }
        Row(
            modifier = modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.Start)
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        appViewModel.navigateTo(ScreenRoute.GoogleProfile)
                    }
                ),
        ) {
            Icon(
                modifier = modifier
                    .padding(8.dp),
                imageVector = Icons.Default.SupervisedUserCircle,
                contentDescription = null,
            )
            Text(
                modifier = modifier
                    .padding(8.dp),
                text = "Google Profile Settings",
            )
        }
        if (WitnessBuildConfig.DEBUG_MODE) {
            Row(
                modifier = modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Start)
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            appViewModel.navigateTo(ScreenRoute.DebugScreen)
                        }
                    ),
            ) {
                Icon(
                    modifier = modifier
                        .padding(8.dp),
                    imageVector = Icons.Default.NotificationImportant,
                    contentDescription = null,
                )
                Text(
                    modifier = modifier
                        .padding(8.dp),
                    text = "Debug Mode",
                )
            }
        }
    }
}
