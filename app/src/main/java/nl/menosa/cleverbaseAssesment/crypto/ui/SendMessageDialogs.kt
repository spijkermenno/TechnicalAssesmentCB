package nl.menosa.cleverbaseAssesment.crypto.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import nl.menosa.cleverbaseAssesment.friendlist.Friend

@Composable
fun SendMessageDialog(
    friend: Friend,
    onDismiss: () -> Unit,
    onSubmit: (message: String) -> Boolean,
) {
    var message by remember(friend.id) { mutableStateOf("") }
    var errorMessage by remember(friend.id) { mutableStateOf<String?>(null) }
    val canSubmit = message.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Message to ${friend.nickname}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    label = { Text("Message") },
                    placeholder = { Text("Type your message…") }
                )
                errorMessage?.let { msg ->
                    Text(
                        msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (onSubmit(message.trim())) {
                        onDismiss()
                    } else {
                        errorMessage = "Could not encrypt. Check the message and your friend’s public key."
                    }
                },
                enabled = canSubmit
            ) {
                Text("Encrypt")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private enum class EncryptedPayloadStep {
    ChooseAction,
    ShowQr,
}

@Composable
fun EncryptedPayloadActionsDialog(
    encryptedText: String,
    onDismiss: () -> Unit,
) {
    var step by remember(encryptedText) { mutableStateOf(EncryptedPayloadStep.ChooseAction) }
    val context = LocalContext.current

    when (step) {
        EncryptedPayloadStep.ChooseAction -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Message encrypted") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Share the ciphertext or show it as a QR code for the other person to scan.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = {
                                ShareCompat.IntentBuilder(context)
                                    .setType("text/plain")
                                    .setText(encryptedText)
                                    .setChooserTitle("Share encrypted message")
                                    .startChooser()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Share")
                        }
                        OutlinedButton(
                            onClick = { step = EncryptedPayloadStep.ShowQr },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("QR code")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            )
        }

        EncryptedPayloadStep.ShowQr -> {
            val bitmap = remember(encryptedText) { encodeTextAsQrBitmap(encryptedText) }
            AlertDialog(
                onDismissRequest = { step = EncryptedPayloadStep.ChooseAction },
                title = { Text("QR code") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Encrypted message as QR code",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                "Could not build a QR code for this text (it may be too long). Use Share instead.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { step = EncryptedPayloadStep.ChooseAction }) {
                        Text("Back")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Done")
                    }
                }
            )
        }
    }
}
