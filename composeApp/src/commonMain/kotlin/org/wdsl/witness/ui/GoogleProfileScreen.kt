package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.viewmodel.GoogleOAuthUiState
import org.wdsl.witness.viewmodel.GoogleOAuthViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun GoogleProfileScreen(
    modifier: Modifier = Modifier,
    googleOAuthViewModel: GoogleOAuthViewModel = witnessViewModel(factory = GoogleOAuthViewModel.Factory)
) {
    val platformContext = LocalPlatformContext.current
    LaunchedEffect(Unit) {
        googleOAuthViewModel.initialize()
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val googleOAuthUiState by googleOAuthViewModel.googleOAuthUiState.collectAsStateWithLifecycle()
        when (googleOAuthUiState) {
            is GoogleOAuthUiState.NoProfile -> {
                Button(
                    modifier = modifier
                        .padding(16.dp),
                    onClick = {
                        googleOAuthViewModel.startGoogleOAuthFlow(
                            platformContext = platformContext,
                        )
                    },
                ) {
                    Text(
                        modifier = modifier,
                        text = "Start Google OAuth Flow",
                    )
                }
                return
            }

            is GoogleOAuthUiState.Error -> {
                Text(
                    modifier = modifier
                        .padding(16.dp),
                    text = "Error: ${(googleOAuthUiState as GoogleOAuthUiState.Error).message}",
                )
                return
            }

            is GoogleOAuthUiState.Loading -> {
                Text(
                    modifier = modifier
                        .padding(16.dp),
                    text = "Loading...",
                )
                return
            }

            else -> {
                val profile = (googleOAuthUiState as GoogleOAuthUiState.Profile).googleProfile

                val imageLoader = ImageLoader(platformContext.context as PlatformContext)
                AsyncImage(
                    modifier = modifier
                        .padding(8.dp),
                    model = profile.picture,
                    contentDescription = null,
                    imageLoader = imageLoader,
                )
                Text(
                    modifier = modifier
                        .padding(8.dp),
                    text = profile.name,
                )
            }
        }
    }
}
