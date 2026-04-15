package nl.menosa.cleverbaseAssesment.crypto.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.menosa.cleverbaseAssesment.crypto.CryptoViewModel
import nl.menosa.cleverbaseAssesment.friendlist.Friend
import nl.menosa.cleverbaseAssesment.friendlist.SaveResult

@Composable
fun CryptoScreen(modifier: Modifier = Modifier, viewModel: CryptoViewModel = viewModel()) {
    var showQrScanner by remember { mutableStateOf(false) }
    var showDecryptResult by remember { mutableStateOf(false) }
    var sendToFriend by remember { mutableStateOf<Friend?>(null) }
    var showEncryptedPayloadActions by remember { mutableStateOf(false) }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IdentityCard(
                publicKey = viewModel.personalPublicKey,
                onGenerateClick = { viewModel.generateKeys() },
                openQRScanner = { showQrScanner = true }
            )

            FriendsList(
                modifier = Modifier.fillMaxWidth(),
                friends = viewModel.friends,
                onAddFriend = viewModel::addFriend,
                onSendMessage = { sendToFriend = it },
                onRemoveFriend = { viewModel.removeFriend(it.id) }
            )
        }

        if (showQrScanner) {
            QrScannerOverlay(
                onDismiss = { showQrScanner = false },
                onQrDecoded = { raw ->
                    viewModel.decrypt(raw)
                    showQrScanner = false
                    showDecryptResult = true
                }
            )
        }
    }

    sendToFriend?.let { friend ->
        SendMessageDialog(
            friend = friend,
            onDismiss = { sendToFriend = null },
            onSubmit = { message ->
                val ok = viewModel.encrypt(friend.publicKeyBase64, message)
                if (ok) {
                    showEncryptedPayloadActions = true
                }
                ok
            }
        )
    }

    if (showDecryptResult) {
        AlertDialog(
            onDismissRequest = { showDecryptResult = false },
            title = { Text("Decrypted message") },
            text = {
                Text(
                    viewModel.decryptedText,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showDecryptResult = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showEncryptedPayloadActions) {
        EncryptedPayloadActionsDialog(
            encryptedText = viewModel.encryptedText,
            onDismiss = { showEncryptedPayloadActions = false }
        )
    }
}

@Composable
fun FriendsList(
    modifier: Modifier = Modifier,
    friends: List<Friend>,
    onAddFriend: (nickname: String, publicKeyBase64: String) -> SaveResult,
    onSendMessage: (Friend) -> Unit,
    onRemoveFriend: (Friend) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Friends")
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add friend")
                }
            }

            if (friends.isEmpty()) {
                Text(
                    "No friends yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    friends.forEach { friend ->
                        FriendCard(
                            modifier = Modifier.fillMaxWidth(),
                            friend = friend,
                            onSendMessage = { onSendMessage(friend) },
                            onRemove = { onRemoveFriend(friend) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddFriendDialog(
            onDismiss = { showAddDialog = false },
            onAdd = onAddFriend,
        )
    }
}

@Composable
private fun AddFriendDialog(
    onDismiss: () -> Unit,
    onAdd: (nickname: String, publicKeyBase64: String) -> SaveResult,
) {
    var nickname by remember { mutableStateOf("") }
    var publicKey by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val canSubmit = nickname.isNotBlank() && publicKey.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add friend") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nickname") }
                )
                OutlinedTextField(
                    value = publicKey,
                    onValueChange = {
                        publicKey = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    label = { Text("Public key") }
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
                    when (
                        val result = onAdd(nickname.trim(), publicKey.trim())
                    ) {
                        is SaveResult.Success -> onDismiss()
                        is SaveResult.Error -> errorMessage = result.message
                    }
                },
                enabled = canSubmit
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendCard(
    modifier: Modifier = Modifier,
    friend: Friend,
    onSendMessage: () -> Unit,
    onRemove: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                val swipeHintColor = MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                Box(Modifier.fillMaxWidth()) {

                    Row(
                        Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = friend.nickname,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        IconButton(onClick = onSendMessage) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send message",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        },
    )
}
