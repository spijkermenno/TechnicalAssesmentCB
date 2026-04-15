package nl.menosa.cleverbaseAssesment.crypto.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.menosa.cleverbaseAssesment.crypto.CryptoViewModel
import nl.menosa.cleverbaseAssesment.friendlist.Friend

@Composable
fun CryptoScreen(modifier: Modifier = Modifier, viewModel: CryptoViewModel = viewModel()) {
    // collapsable sections
    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IdentityCard(
            publicKey = viewModel.personalPublicKey
        ) {
            viewModel.generateKeys()
        }

        FriendsList(Modifier, emptyList())
    }
}

@Composable
fun FriendsList(modifier: Modifier = Modifier, friends: List<Friend>) {
    Card(modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = {

        }) {
            Icon(Icons.Rounded.Add, contentDescription = "Add button")
        }

        Column {
            friends.forEach {
                FriendCard(modifier = Modifier.padding(8.dp), friend = it)
            }
        }
    }
}

@Composable
fun FriendCard(modifier: Modifier = Modifier, friend: Friend) {
    Card() {
        Text(friend.nickname)
        Button(onClick = {}) {
            Text("Send message")
        }
    }
}