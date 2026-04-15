package nl.menosa.cleverbaseAssesment.crypto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IdentityCard(
    modifier: Modifier = Modifier,
    publicKey: String?,
    onGenerateClick: () -> Unit,
    openQRScanner: () -> Unit
) {
    var showPublicKeyActions by remember { mutableStateOf(false) }

    val keyForDialog = publicKey
    if (showPublicKeyActions && !keyForDialog.isNullOrEmpty()) {
        PublicKeyActionsDialog(
            publicKey = keyForDialog,
            onDismiss = { showPublicKeyActions = false }
        )
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Personal Public Key")

                if (publicKey == null) {
                    Button({ onGenerateClick() }) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "Add button"
                        )
                    }
                } else {
                    Button({ openQRScanner() }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Scan a QR message"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectionContainer(
                    Modifier
                        .background(Color.LightGray, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                        .fillMaxWidth(0.7f)
                ) {
                    if (publicKey.isNullOrEmpty()) {
                        Text("No key available")
                    } else {
                        Text(publicKey, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }

                Button(
                    onClick = { showPublicKeyActions = true },
                    enabled = !publicKey.isNullOrEmpty()
                ) {
                    Icon(
                        Icons.Rounded.Share,
                        contentDescription = "Copy, share, or show QR"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun IdentityManagementPreview() {
    Column {
        IdentityCard(
            modifier = Modifier.padding(8.dp),
            publicKey = "TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY TEST PUBLIC KEY",
            onGenerateClick = {},
            openQRScanner = {}
        )

        IdentityCard(
            modifier = Modifier.padding(8.dp),
            publicKey = "",
            onGenerateClick = {},
            openQRScanner = {}
        )

        IdentityCard(
            modifier = Modifier.padding(8.dp),
            publicKey = null,
            onGenerateClick = {},
            openQRScanner = {}
        )
    }
}