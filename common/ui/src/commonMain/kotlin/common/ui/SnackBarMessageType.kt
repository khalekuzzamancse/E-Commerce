package common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

enum class SnackBarMessageType {
    Error, Success
}

data class SnackBarMessage(
    val message: String,
    val type: SnackBarMessageType,
    val details: String? = null,
)

/**
 * @param onDismissRequest make message as null
 */
@Composable
fun CustomSnackBar(
    message: SnackBarMessage,
    onDismissRequest: () -> Unit,
) {

    val containerColor = if (message.type == SnackBarMessageType.Error) Color.Red else Color.Green
    val textColor = if (message.type == SnackBarMessageType.Error) Color.White else Color.Black
    var showDetails by remember { mutableStateOf(false) }
    val action: @Composable () -> Unit = {
        TextButton(
            onClick = {
                showDetails = true;
            }
        ) {
            Text("Details")
        }
    }
    if (showDetails && message.details != null) {
        _DetailsDialogue(message.details, onDismissRequest = {
            onDismissRequest()
          //  showDetails = false
        })
    }


    Snackbar(
        modifier = Modifier.padding(16.dp),
        containerColor = containerColor,
        contentColor = textColor,
        action = if (message.details != null) action else null
    ) {
        Text(message.message)
    }


}

@Composable
private fun _DetailsDialogue(
    details: String,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        dismissButton = {
            TextButton(onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(details)
            }
        }
    )

}
