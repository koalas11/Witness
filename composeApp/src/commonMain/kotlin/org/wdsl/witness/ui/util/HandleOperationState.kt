package org.wdsl.witness.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.LocalNotificationsSetting
import org.wdsl.witness.model.NotificationType
import org.wdsl.witness.model.NotificationsSetting
import org.wdsl.witness.viewmodel.BaseOperationViewModel
import org.wdsl.witness.viewmodel.OperationUiState

@Composable
fun handleOperationState(
    viewModel: BaseOperationViewModel,
    onSuccess: () -> Unit = { viewModel.resetOperationState() },
    onError: () -> Unit = { viewModel.resetOperationState() },
    notificationType: NotificationType = NotificationType.INFO,
): Boolean {
    val operationState by viewModel.operationUiState.collectAsStateWithLifecycle()
    val enabled = operationState is OperationUiState.Idle

    if (operationState is OperationUiState.Success) {
        if (LocalNotificationsSetting.current == NotificationsSetting.ALL_NOTIFICATIONS
            && (operationState as OperationUiState.Success).message != null) {
            fastUIActions.DisplayNotification(
                message = (operationState as OperationUiState.Success).message!!,
                notificationType = notificationType
            )
        }

        onSuccess()
    } else if (operationState is OperationUiState.Error) {
        fastUIActions.DisplayNotification(
            message = (operationState as OperationUiState.Error).message,
            notificationType = NotificationType.ERROR
        )
        onError()
    }

    return enabled
}
