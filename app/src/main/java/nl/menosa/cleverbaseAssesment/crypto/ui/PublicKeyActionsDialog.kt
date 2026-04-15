package nl.menosa.cleverbaseAssesment.crypto.ui

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat

private enum class PublicKeyStep {
    ChooseAction,
    ShowQr,
}

@Composable
fun PublicKeyActionsDialog(
    publicKey: String,
    onDismiss: () -> Unit,
) {
    var step by remember(publicKey) { mutableStateOf(PublicKeyStep.ChooseAction) }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    when (step) {
        PublicKeyStep.ChooseAction -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Your public key") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Copy the key, send it with the share sheet, or show it as a QR code.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { clipboard.setText(AnnotatedString(publicKey)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Copy")
                        }
                        OutlinedButton(
                            onClick = {
                                ShareCompat.IntentBuilder(context)
                                    .setType("text/plain")
                                    .setText(publicKey)
                                    .setChooserTitle("Share public key")
                                    .startChooser()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Share")
                        }
                        OutlinedButton(
                            onClick = { step = PublicKeyStep.ShowQr },
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

        PublicKeyStep.ShowQr -> {
            val bitmap = remember(publicKey) { encodeTextAsQrBitmap(publicKey) }
            AlertDialog(
                onDismissRequest = { step = PublicKeyStep.ChooseAction },
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
                                contentDescription = "Public key as QR code",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                "Could not build a QR code for this key (it may be too long). Use Copy or Share instead.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { step = PublicKeyStep.ChooseAction }) {
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
