package org.wdsl.witness

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    AppSetup(
        appContainer = IOSAppContainer(),
    )
}
