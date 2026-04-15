package nl.menosa.cleverbaseAssesment.crypto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nl.menosa.cleverbaseAssesment.friendlist.Friend
import nl.menosa.cleverbaseAssesment.friendlist.FriendListRepository
import nl.menosa.cleverbaseAssesment.friendlist.SaveResult

class CryptoViewModel(
    private val cryptoManager: CryptoManager,
    private val friendListRepository: FriendListRepository
) : ViewModel() {
    var personalPublicKey by mutableStateOf<String?>(null)
    var friends by mutableStateOf<List<Friend>>(emptyList())

    var encryptedText by mutableStateOf("")
    var decryptedText by mutableStateOf("")

    var saveErrorMessage by mutableStateOf<String?>(null)

    init {
        refreshPublicKey()
    }

    fun generateKeys() {
        cryptoManager.generateKeyPairsIfNeeded()
        refreshPublicKey()
    }

    fun encrypt(key: String, message: String) {
        encryptedText =
            cryptoManager.encryptWithOtherPublicKey(key, message)
                ?: "Something went wrong while encrypting..."
    }

    fun decrypt(encodedText: String) {
        decryptedText =
            cryptoManager.decrypt(encodedText) ?: "Something went wrong while decrypting..."

    }

    private fun refreshPublicKey() {
        personalPublicKey = cryptoManager.getPublicKeyBase64()
    }

    fun addFriend(nickname: String, publicKeyBase64: String) {
        val result = friendListRepository.saveFriend(nickname, publicKeyBase64)

        if (result is SaveResult.Error) {
            saveErrorMessage = result.message
        } else {
            refreshFriends()
        }
    }

    fun removeFriend(friendId: String) {
        friendListRepository.removeFriend(friendId)
        refreshFriends()
    }

    private fun refreshFriends() {
        friends = friendListRepository.getAllFriends()
    }
}